package be.vbgn.gradle.buildaspects.internal;

import java.util.LinkedList;
import java.util.List;
import org.gradle.api.Action;

public class EventDispatcher<T> {

    private final List<Action<T>> eventListeners = new LinkedList<>();

    public void addListener(Action<T> listener) {
        eventListeners.add(listener);
    }

    public void fire(T context) {
        for (Action<T> eventListener : eventListeners) {
            eventListener.execute(context);
        }
    }
}
