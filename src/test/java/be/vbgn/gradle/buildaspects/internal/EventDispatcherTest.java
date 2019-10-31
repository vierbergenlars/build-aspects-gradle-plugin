package be.vbgn.gradle.buildaspects.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class EventDispatcherTest {

    @Test
    public void addListenerAndFire() {
        EventDispatcher<Boolean> dispatcher = new EventDispatcher<>();
        AtomicBoolean listenerValueA = new AtomicBoolean(false);
        AtomicBoolean listenerValueB = new AtomicBoolean(false);

        dispatcher.addListener(listenerValueA::set);
        dispatcher.addListener(listenerValueB::set);

        dispatcher.fire(true);

        assertTrue(listenerValueA.get());
        assertTrue(listenerValueB.get());

        dispatcher.fire(false);

        assertFalse(listenerValueA.get());
        assertFalse(listenerValueB.get());
    }

    @Test
    public void listenerOrdering() {
        EventDispatcher<Boolean> dispatcher = new EventDispatcher<>();
        AtomicBoolean listenerValueA = new AtomicBoolean(false);
        AtomicBoolean listenerValueB = new AtomicBoolean(false);

        dispatcher.addListener(listenerValueA::set);
        dispatcher.addListener(value -> {
            assertEquals(value, listenerValueA.get());
            listenerValueB.set(value);
        });

        dispatcher.fire(true);

        assertTrue(listenerValueA.get());
        assertTrue(listenerValueB.get());
    }

}
