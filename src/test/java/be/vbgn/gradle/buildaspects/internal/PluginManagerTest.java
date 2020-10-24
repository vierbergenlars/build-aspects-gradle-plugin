package be.vbgn.gradle.buildaspects.internal;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.atomic.AtomicInteger;
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

    @Test
    public void applyPluginMultipleTimes() {
        PluginManager<Object> pluginManager = new PluginManager<>();
        Plugin<Object> plugin1 = Mockito.mock(Plugin.class);
        Object buildAspects1 = new Object();
        pluginManager.apply(buildAspects1);

        pluginManager.applyPlugin(plugin1);
        Mockito.verify(plugin1).apply(buildAspects1);

        pluginManager.applyPlugin(plugin1);
        Mockito.verifyNoMoreInteractions(plugin1);
    }

    @Test
    public void applyPluginClass() {
        PluginManager<Object> pluginManager = new PluginManager<>();
        Object buildAspects1 = new Object();
        pluginManager.apply(buildAspects1);

        int constructCount = TestPlugin.CONSTRUCT_COUNT.get();
        TestPlugin plugin1 = pluginManager.applyPlugin(TestPlugin.class);
        assertEquals(1, TestPlugin.CONSTRUCT_COUNT.get() - constructCount);
        assertEquals(1, plugin1.APPLY_COUNT.get());

        TestPlugin plugin2 = pluginManager.applyPlugin(TestPlugin.class);
        assertEquals(plugin1, plugin2);
        assertEquals(1, TestPlugin.CONSTRUCT_COUNT.get() - constructCount);
        assertEquals(1, plugin1.APPLY_COUNT.get());
    }

    public static class TestPlugin implements Plugin<Object> {

        public static final AtomicInteger CONSTRUCT_COUNT = new AtomicInteger(0);
        public final AtomicInteger APPLY_COUNT = new AtomicInteger(0);

        public TestPlugin() {
            CONSTRUCT_COUNT.incrementAndGet();
        }

        @Override
        public void apply(Object o) {
            APPLY_COUNT.incrementAndGet();
        }
    }
}
