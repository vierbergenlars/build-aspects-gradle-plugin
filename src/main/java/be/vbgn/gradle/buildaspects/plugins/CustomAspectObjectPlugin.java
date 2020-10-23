package be.vbgn.gradle.buildaspects.plugins;

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

public class CustomAspectObjectPlugin<T, U> implements Plugin<BuildAspects> {

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @Getter(AccessLevel.PRIVATE)
    public static final class Configuration<T, U> {

        private final Class<T> aspectType;
        private final Class<U> extensionType;
        private final String extensionName;
        private final String aspectName;
        private final BiConsumer<AspectHandler, Function<Variant, T>> createCalculatedAspects;

        private void createCalculatedAspects(AspectHandler aspectHandler) {
            createCalculatedAspects.accept(aspectHandler, variant -> variant.getProperty(aspectName, aspectType));
        }

        private static <T, U> ConfigurationBuilder<T, U> builder(Class<T> aspectType, Class<U> extensionType) {
            return new ConfigurationBuilder<T, U>(aspectType, extensionType);
        }

        public static final class ConfigurationBuilder<T, U> {

            private final Class<T> aspectType;
            private final Class<U> extensionType;
            private String extensionName;
            private String aspectName;
            private BiConsumer<AspectHandler, Function<Variant, T>> createCalculatedAspects = (_aspectHandler, _getVariant) -> {
            };

            private ConfigurationBuilder(Class<T> aspectType, Class<U> extensionType) {
                this.aspectType = aspectType;
                this.extensionType = extensionType;
                this.extensionName = extensionType.getSimpleName();
            }

            public ConfigurationBuilder<T, U> extensionName(String extensionName) {
                this.extensionName = extensionName;
                return this;
            }

            public ConfigurationBuilder<T, U> aspectName(String aspectName) {
                this.aspectName = aspectName;
                return this;
            }

            public ConfigurationBuilder<T, U> createCalculatedAspects(
                    BiConsumer<AspectHandler, Function<Variant, T>> createCalculatedAspects) {
                this.createCalculatedAspects = createCalculatedAspects;
                return this;
            }

            public Configuration<T, U> build() {
                String aspectName = this.aspectName == null ? this.extensionName : this.aspectName;
                return new Configuration<T, U>(aspectType, extensionType, extensionName, aspectName,
                        createCalculatedAspects);
            }

        }
    }

    private final Configuration<T, U> configuration;

    public CustomAspectObjectPlugin(
            Configuration<T, U> configuration) {
        this.configuration = configuration;
    }

    @Override
    public void apply(BuildAspects buildAspects) {
        List<T> customObjects = new ArrayList<>();
        EventDispatcher<T> addCustomObjectDispatcher = new EventDispatcher<T>();
        addCustomObjectDispatcher.addListener(customObjects::add);

        Consumer<T> customObjectAdded = addCustomObjectDispatcher::fire;

        buildAspects.getExtensions()
                .create(configuration.getExtensionName(), configuration.getExtensionType(), customObjectAdded);
        buildAspects.beforeAspectsCalculated(_empty -> {
            buildAspects.getAspects()
                    .create(configuration.getAspectName(), configuration.getAspectType(), customObjects);
            configuration.createCalculatedAspects(buildAspects.getAspects());
            addCustomObjectDispatcher.addListener(_version -> {
                throw IllegalBuildAspectsStateException.modifyAfterProjectsAdded(configuration.getExtensionName());
            });
        });
    }

    public static <T, U> Configuration.ConfigurationBuilder<T, U> configuration(Class<T> aspectType,
            Class<U> extensionType) {
        return Configuration.builder(aspectType, extensionType);
    }

}
