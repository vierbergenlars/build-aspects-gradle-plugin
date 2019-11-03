package be.vbgn.gradle.buildaspects.aspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class AspectHandlerTest {

    @Test
    public void createAspects() {
        AspectHandler handler = new AspectHandler();
        Aspect<String> systemVersionAspect = handler.create("systemVersion", String.class);
        Aspect<Boolean> isCommunityAspect = handler.create("community", Boolean.class);

        Collection<Aspect<?>> aspects = handler.getAspects();
        assertEquals(Arrays.asList(systemVersionAspect, isCommunityAspect), aspects);
    }

    @Test
    public void createComponents() {
        AspectHandler handler = new AspectHandler();
        Aspect<String> systemVersionAspect = handler.create("systemVersion", String.class);
        systemVersionAspect.add("1.0");
        systemVersionAspect.add("1.2");
        systemVersionAspect.add("2.0");
        Aspect<Boolean> isCommunityAspect = handler.create("community", Boolean.class);
        isCommunityAspect.add(true);
        isCommunityAspect.add(false);

        Collection<Component> components = handler.getComponents();

        assertEquals(6, components.size());
        List<Component> componentsList = new ArrayList<>(components);

        assertEquals(new HashMap<>() {{
            put("systemVersion", "1.0");
            put("community", true);
        }}, componentsList.get(0).toMap());
        assertEquals(new HashMap<>() {{
            put("systemVersion", "1.0");
            put("community", false);
        }}, componentsList.get(1).toMap());

        assertEquals(new HashMap<>() {{
            put("systemVersion", "1.2");
            put("community", true);
        }}, componentsList.get(2).toMap());
        assertEquals(new HashMap<>() {{
            put("systemVersion", "1.2");
            put("community", false);
        }}, componentsList.get(3).toMap());

        assertEquals(new HashMap<>() {{
            put("systemVersion", "2.0");
            put("community", true);
        }}, componentsList.get(4).toMap());
        assertEquals(new HashMap<>() {{
            put("systemVersion", "2.0");
            put("community", false);
        }}, componentsList.get(5).toMap());
    }

    @Test(expected = IllegalArgumentException.class)
    public void createDuplicateAspect() {
        AspectHandler handler = new AspectHandler();
        handler.create("test1", String.class);
        handler.create("test1", String.class);
    }

    @Test
    public void createAspectFiresListener() {
        AspectHandler handler = new AspectHandler();
        AtomicBoolean handlerFired = new AtomicBoolean(false);

        handler.aspectAdded(a -> handlerFired.set(true));

        assertFalse(handlerFired.get());

        handler.create("aspect", String.class);

        assertTrue(handlerFired.get());
    }

}
