package be.vbgn.gradle.buildaspects.plugins.prototype;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.internal.EventDispatcher;
import be.vbgn.gradle.buildaspects.settings.dsl.BuildAspects;
import be.vbgn.gradle.buildaspects.settings.dsl.IllegalBuildAspectsStateException;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.gradle.api.Plugin;

/**
 * Plugin prototype that adds a custom extension to `buildAspects {}` to make definition of a common aspect more ergonomic.
 * <p>
 * For example:
 *
 * <pre>
 * buildAspects {
 *     system {
 *         version "1.2" withDatabase "2.3"
 *         version "1.3" withDatabase "2.4"
 *     }
 *     projects {
 *         include(":abc")
 *     }
 * }
 * </pre>
 * <p>
 * Can automatically be mapped to:
 *
 * <pre>
 * buildAspects {
 *    aspects {
 *        create("system", new SystemVersion("1.2", "2.3"), new SystemVersion("1.3", "2.4"))
 *    }
 *    projects {
 *        include(":abc")
 *    }
 * }
 * </pre>
 *
 * @param <A> Type of the aspect that will be added. Can be an arbitrary object with properties, or a primitive.
 * @param <E> Type of the extension that will be added to `buildAspects {}`
 */
public class CustomAspectObjectPluginPrototype<A, E> implements Plugin<BuildAspects> {

    @FunctionalInterface
    public interface CreateCalculatedProperties<A> extends BiConsumer<AspectHandler, Function<Variant, A>> {

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    public static final class Configuration<A, E> {

        private final Class<A> aspectType;
        private final Class<E> extensionType;
        private final String extensionName;
        private final String aspectName;
        private final CreateCalculatedProperties<A> createCalculatedProperties;

        private void createCalculatedProperties(AspectHandler aspectHandler) {
            createCalculatedProperties.accept(aspectHandler, variant -> variant.getProperty(aspectName, aspectType));
        }

        private static <A, E> ConfigurationBuilder<A, E> builder(Class<A> aspectType, Class<E> extensionType) {
            return new ConfigurationBuilder<>(aspectType, extensionType);
        }

        public static final class ConfigurationBuilder<A, E> {

            private final Class<A> aspectType;
            private final Class<E> extensionType;
            private String extensionName;
            private String aspectName;
            private CreateCalculatedProperties<A> createCalculatedProperties = (_aspectHandler, _getVariant) -> {
            };

            private ConfigurationBuilder(Class<A> aspectType, Class<E> extensionType) {
                this.aspectType = aspectType;
                this.extensionType = extensionType;
                this.extensionName = extensionType.getSimpleName();
            }

            /**
             * Configures the name of the extension block that is added to `buildAspects {}`. Defaults to the simple classname of the extension type.
             *
             * @param extensionName A custom name for the extension block
             * @return itself, for chaining
             */
            public ConfigurationBuilder<A, E> extensionName(String extensionName) {
                this.extensionName = extensionName;
                return this;
            }

            /**
             * Configures the name of the aspect that is added. Defaults to the extension name.
             *
             * @param aspectName A custom name for the aspect that is added
             * @return itself, for chaining
             */
            public ConfigurationBuilder<A, E> aspectName(String aspectName) {
                this.aspectName = aspectName;
                return this;
            }

            /**
             * Adds factory method for calculated properties that are derived from the aspect that is added by this plugin
             * <p>
             * For example:
             * <pre>
             *    createCalculatedProperties((aspectHandler, getVariant) -&gt; {
             *        aspectHandler.calculated("systemVersion", getVariant.andThen(SystemVersion::getVersion))
             *        aspectHandler.calculated("dbVersion", getVariant.andThen(SystemVersion::getDatabaseVersion))
             *    });
             * </pre>
             * <p>
             * Adds two calculated aspects, based on the "system" aspect of type SystemVersion that was created earlier.
             *
             * @param createCalculatedProperties Factory method to create calculated properties
             * @return itself, for chaining
             */
            public ConfigurationBuilder<A, E> createCalculatedProperties(
                    CreateCalculatedProperties<A> createCalculatedProperties) {
                this.createCalculatedProperties = createCalculatedProperties;
                return this;
            }

            public Configuration<A, E> build() {
                String newAspectName = this.aspectName == null ? this.extensionName : this.aspectName;
                return new Configuration<>(aspectType, extensionType, extensionName, newAspectName,
                        createCalculatedProperties);
            }

        }
    }

    private final Configuration<A, E> configuration;

    public CustomAspectObjectPluginPrototype(Configuration<A, E> configuration) {
        this.configuration = configuration;
    }

    @Override
    public void apply(BuildAspects buildAspects) {
        List<A> customObjects = new ArrayList<>();
        EventDispatcher<A> addCustomObjectDispatcher = new EventDispatcher<>();
        addCustomObjectDispatcher.addListener(customObjects::add);

        buildAspects.getExtensions().create(configuration.getExtensionName(), configuration.getExtensionType(),
                (Consumer<A>) addCustomObjectDispatcher::fire);
        buildAspects.beforeAspectsCalculated(_empty -> {
            buildAspects.getAspects()
                    .create(configuration.getAspectName(), configuration.getAspectType(), customObjects);
            configuration.createCalculatedProperties(buildAspects.getAspects());
            addCustomObjectDispatcher.addListener(_version -> {
                throw IllegalBuildAspectsStateException.modifyAfterProjectsAdded(configuration.getExtensionName());
            });
        });
    }

    /**
     * Creates a configuration builder for this plugin
     *
     * @param aspectType    The type of the aspect that will be added to BuildAspects.aspects
     * @param extensionType The type of the extension that will be added to BuildAspects
     * @param <A>           The type of the aspect that will be added to BuildAspects.aspects
     * @param <E>           The type of the extension that will be added to BuildAspects
     * @return Configuration builder for this plugin
     */
    public static <A, E> Configuration.ConfigurationBuilder<A, E> configuration(Class<A> aspectType,
            Class<E> extensionType) {
        return Configuration.builder(aspectType, extensionType);
    }

}
