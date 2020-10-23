package be.vbgn.gradle.buildaspects.internal;

import be.vbgn.gradle.buildaspects.plugins.PluginAware;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.gradle.api.Plugin;
import org.gradle.internal.reflect.DirectInstantiator;

public class PluginManager<T> implements Plugin<T>, PluginAware<T> {

    private final List<Plugin<T>> plugins = new ArrayList<>();
    private final Set<T> existingBuildAspects = new HashSet<>();
    private final Map<Class<? extends Plugin<T>>, Plugin<T>> appliedPluginClasses = new HashMap<>();

    @Override
    public void applyPlugin(Plugin<T> plugin) {
        if (plugins.contains(plugin)) {
            // Don't apply the same plugin multiple times
            return;
        }
        plugins.add(plugin);
        for (T existing : existingBuildAspects) {
            plugin.apply(existing);
        }
    }

    @Override
    public <P extends Plugin<T>> P applyPlugin(Class<P> pluginClass) {
        P pluginImpl = (P) appliedPluginClasses.computeIfAbsent(pluginClass, DirectInstantiator::instantiate);
        applyPlugin(pluginImpl);
        return pluginImpl;
    }

    public void apply(T buildAspects) {
        for (Plugin<T> plugin : plugins) {
            plugin.apply(buildAspects);
        }
        existingBuildAspects.add(buildAspects);
    }
}
