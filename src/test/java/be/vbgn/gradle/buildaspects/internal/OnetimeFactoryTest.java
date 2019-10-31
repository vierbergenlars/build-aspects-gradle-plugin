package be.vbgn.gradle.buildaspects.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class OnetimeFactoryTest {

    @Test
    public void simpleBuild() {
        OnetimeFactory<Boolean, String> factory = new OnetimeFactory<>(Object::toString);

        factory.setSource(true);

        String value = factory.build();

        assertEquals("true", value);
    }

    @Test(expected = IllegalStateException.class)
    public void setSourceAfterBuild() {
        OnetimeFactory<Boolean, String> factory = new OnetimeFactory<>(Object::toString);
        factory.setSource(true);
        factory.build();
        factory.setSource(false);
    }

    @Test(expected = NullPointerException.class)
    public void buildWithoutSource() {
        OnetimeFactory<Boolean, String> factory = new OnetimeFactory<>(Object::toString);
        factory.build();

    }

}
