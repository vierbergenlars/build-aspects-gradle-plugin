package be.vbgn.gradle.buildaspects.plugins;

import org.gradle.api.Plugin;

public interface PluginAware<T> {

    void applyPlugin(Plugin<T> plugin);

    <P extends Plugin<T>> P applyPlugin(Class<P> pluginClass);
}
