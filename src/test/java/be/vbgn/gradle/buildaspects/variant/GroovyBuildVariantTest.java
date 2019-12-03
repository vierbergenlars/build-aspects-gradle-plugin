package be.vbgn.gradle.buildaspects.variant;

import static org.junit.Assert.assertEquals;

import be.vbgn.gradle.buildaspects.TestUtil;
import groovy.lang.MissingPropertyException;
import groovy.lang.ReadOnlyPropertyException;
import java.util.Collections;
import org.junit.Test;

public class GroovyBuildVariantTest {

    @Test
    public void getProperty() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));

        GroovyBuildVariant buildVariant = new GroovyBuildVariant(variant);

        assertEquals("1.0", buildVariant.getProperty("systemVersion"));
    }

    @Test(expected = MissingPropertyException.class)
    public void getPropertyNonExisting() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));

        GroovyBuildVariant buildVariant = new GroovyBuildVariant(variant);

        buildVariant.getProperty("nonexistingProperty");
    }

    @Test
    public void getProperties() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));

        GroovyBuildVariant buildVariant = new GroovyBuildVariant(variant);

        assertEquals(variant.getProperties(), buildVariant.getProperties());
        assertEquals(variant.getProperties(), buildVariant.getProperty("properties"));
    }

    @Test(expected = ReadOnlyPropertyException.class)
    public void setProperty() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));

        GroovyBuildVariant buildVariant = new GroovyBuildVariant(variant);

        buildVariant.setProperty("systemVersion", "2.0");
    }

}
