package be.vbgn.gradle.buildaspects.aspect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.Test;

public class AspectHandlerTest {

    @Test
    public void createAspects() {
        AspectHandler handler = new AspectHandler();
        Aspect<String> systemVersionAspect = handler.create("systemVersion", String.class, a -> {
        });
        Aspect<Boolean> isCommunityAspect = handler.create("community", Boolean.class, a -> {
        });

        Collection<Aspect<?>> aspects = handler.getAspects();
        assertEquals(Arrays.asList(systemVersionAspect, isCommunityAspect), aspects);
    }

    @Test(expected = DuplicateAspectNameException.class)
    public void createDuplicateAspect() {
        AspectHandler handler = new AspectHandler();
        handler.create("test1", String.class, a -> {
        });
        handler.create("test1", String.class, a -> {
        });
    }

    @Test
    public void createAspectFiresListener() {
        AspectHandler handler = new AspectHandler();
        AtomicBoolean handlerFired = new AtomicBoolean(false);

        handler.aspectAdded(a -> handlerFired.set(true));

        assertFalse(handlerFired.get());

        handler.create("aspect", String.class, a -> {
        });

        assertTrue(handlerFired.get());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void modifyAspectAfterCreate() {
        AspectHandler handler = new AspectHandler();
        WritableAspect<String> aspect = (WritableAspect<String>) handler.create("test1", String.class, a -> {
        });
        aspect.add("xyz");
    }

}
