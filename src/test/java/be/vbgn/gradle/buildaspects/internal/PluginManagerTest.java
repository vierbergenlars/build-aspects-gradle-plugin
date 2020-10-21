package be.vbgn.gradle.buildaspects.internal;

import org.gradle.api.Plugin;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

public class PluginManagerTest {

    @Test
    public void applyPluginThenBuildAspects() {
        PluginManager<Object> pluginManager = new PluginManager<>();
        Plugin<Object> plugin1 = Mockito.mock(Plugin.class);
        Plugin<Object> plugin2 = Mockito.mock(Plugin.class);
        Object buildAspects1 = new Object();
        Object buildAspects2 = new Object();

        pluginManager.applyPlugin(plugin1);
        pluginManager.applyPlugin(plugin2);

        pluginManager.apply(buildAspects1);

        InOrder inOrder = Mockito.inOrder(plugin1, plugin2);

        inOrder.verify(plugin1).apply(buildAspects1);
        inOrder.verify(plugin2).apply(buildAspects1);

        pluginManager.apply(buildAspects2);

        inOrder.verify(plugin1).apply(buildAspects2);
        inOrder.verify(plugin2).apply(buildAspects2);

        Mockito.verifyNoMoreInteractions(plugin1, plugin2);
    }

    @Test
    public void applyBuildAspectsThenPlugin() {
        PluginManager<Object> pluginManager = new PluginManager<>();
        Plugin<Object> plugin1 = Mockito.mock(Plugin.class);
        Plugin<Object> plugin2 = Mockito.mock(Plugin.class);
        Object buildAspects1 = new Object();
        Object buildAspects2 = new Object();

        pluginManager.apply(buildAspects1);
        pluginManager.apply(buildAspects2);

        pluginManager.applyPlugin(plugin1);

        Mockito.verify(plugin1).apply(buildAspects1);
        Mockito.verify(plugin1).apply(buildAspects2);

        Mockito.verifyNoMoreInteractions(plugin1, plugin2);

        pluginManager.applyPlugin(plugin2);

        Mockito.verify(plugin2).apply(buildAspects1);
        Mockito.verify(plugin2).apply(buildAspects2);

        Mockito.verifyNoMoreInteractions(plugin1, plugin2);
    }

}
