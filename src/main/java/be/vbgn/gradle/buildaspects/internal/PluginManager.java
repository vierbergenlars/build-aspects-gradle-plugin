package be.vbgn.gradle.buildaspects.internal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.gradle.api.Plugin;

public class PluginManager<T> implements Plugin<T> {

    private final List<Plugin<T>> plugins = new ArrayList<>();
    private final Set<T> existingBuildAspects = new HashSet<>();

    public void applyPlugin(Plugin<T> plugin) {
        plugins.add(plugin);
        for (T existing : existingBuildAspects) {
            plugin.apply(existing);
        }
    }

    public void apply(T buildAspects) {
        for (Plugin<T> plugin : plugins) {
            plugin.apply(buildAspects);
        }
        existingBuildAspects.add(buildAspects);
    }
}
