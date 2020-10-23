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
 * @param <Aspect>    Type of the aspect that will be added. Can be an arbitrary object with properties, or a primitive.
 * @param <Extension> Type of the extension that will be added to `buildAspects {}`
 */
public class CustomAspectObjectPluginPrototype<Aspect, Extension> implements Plugin<BuildAspects> {

    @FunctionalInterface
    public interface CreateCalculatedProperties<Aspect> extends BiConsumer<AspectHandler, Function<Variant, Aspect>> {

    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    public static final class Configuration<Aspect, Extension> {

        private final Class<Aspect> aspectType;
        private final Class<Extension> extensionType;
        private final String extensionName;
        private final String aspectName;
        private final CreateCalculatedProperties<Aspect> createCalculatedProperties;

        private void createCalculatedProperties(AspectHandler aspectHandler) {
            createCalculatedProperties.accept(aspectHandler, variant -> variant.getProperty(aspectName, aspectType));
        }

        private static <Aspect, Extension> ConfigurationBuilder<Aspect, Extension> builder(Class<Aspect> aspectType,
                Class<Extension> extensionType) {
            return new ConfigurationBuilder<Aspect, Extension>(aspectType, extensionType);
        }

        public static final class ConfigurationBuilder<Aspect, Extension> {

            private final Class<Aspect> aspectType;
            private final Class<Extension> extensionType;
            private String extensionName;
            private String aspectName;
            private CreateCalculatedProperties<Aspect> createCalculatedProperties = (_aspectHandler, _getVariant) -> {
            };

            private ConfigurationBuilder(Class<Aspect> aspectType, Class<Extension> extensionType) {
                this.aspectType = aspectType;
                this.extensionType = extensionType;
                this.extensionName = extensionType.getSimpleName();
            }

            /**
             * Configures the name of the extension block that is added to `buildAspects {}`. Defaults to the simple classname of the extension type.
             *
             * @param extensionName A custom name for the extension block
             */
            public ConfigurationBuilder<Aspect, Extension> extensionName(String extensionName) {
                this.extensionName = extensionName;
                return this;
            }

            /**
             * Configures the name of the aspect that is added. Defaults to the extension name.
             *
             * @param aspectName A custom name for the aspect that is added
             */
            public ConfigurationBuilder<Aspect, Extension> aspectName(String aspectName) {
                this.aspectName = aspectName;
                return this;
            }

            /**
             * Adds factory method for calculated properties that are derived from the aspect that is added by this plugin
             * <p>
             * For example:
             * <pre>
             *    createCalculatedProperties((aspectHandler, getVariant) -> {
             *        aspectHandler.calculated("systemVersion", getVariant.andThen(SystemVersion::getVersion))
             *        aspectHandler.calculated("dbVersion", getVariant.andThen(SystemVersion::getDatabaseVersion))
             *    });
             * </pre>
             * <p>
             * Adds two calculated aspects, based on the "system" aspect of type SystemVersion that was created earlier.
             */
            public ConfigurationBuilder<Aspect, Extension> createCalculatedProperties(
                    CreateCalculatedProperties<Aspect> createCalculatedProperties) {
                this.createCalculatedProperties = createCalculatedProperties;
                return this;
            }

            public Configuration<Aspect, Extension> build() {
                String aspectName = this.aspectName == null ? this.extensionName : this.aspectName;
                return new Configuration<Aspect, Extension>(aspectType, extensionType, extensionName, aspectName,
                        createCalculatedProperties);
            }

        }
    }

    private final Configuration<Aspect, Extension> configuration;

    public CustomAspectObjectPluginPrototype(Configuration<Aspect, Extension> configuration) {
        this.configuration = configuration;
    }

    @Override
    public void apply(BuildAspects buildAspects) {
        List<Aspect> customObjects = new ArrayList<>();
        EventDispatcher<Aspect> addCustomObjectDispatcher = new EventDispatcher<Aspect>();
        addCustomObjectDispatcher.addListener(customObjects::add);

        buildAspects.getExtensions()
                .create(configuration.getExtensionName(), configuration.getExtensionType(),
                        (Consumer<Aspect>) addCustomObjectDispatcher::fire);
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
     * @param <Aspect>      The type of the aspect that will be added to BuildAspects.aspects
     * @param <Extension>   The type of the extension that will be added to BuildAspects
     * @return Configuration builder for this plugin
     */
    public static <Aspect, Extension> Configuration.ConfigurationBuilder<Aspect, Extension> configuration(
            Class<Aspect> aspectType,
            Class<Extension> extensionType) {
        return Configuration.builder(aspectType, extensionType);
    }

}
