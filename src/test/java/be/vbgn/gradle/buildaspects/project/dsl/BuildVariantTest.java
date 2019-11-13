package be.vbgn.gradle.buildaspects.project.dsl;

import static org.junit.Assert.assertEquals;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.variant.Variant;
import groovy.lang.MissingPropertyException;
import groovy.lang.ReadOnlyPropertyException;
import java.util.Collections;
import org.junit.Test;

public class BuildVariantTest {

    @Test
    public void getProperty() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));

        BuildVariant buildVariant = new BuildVariant(variant);

        assertEquals("1.0", buildVariant.getProperty("systemVersion"));
    }

    @Test(expected = MissingPropertyException.class)
    public void getPropertyNonExisting() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));

        BuildVariant buildVariant = new BuildVariant(variant);

        buildVariant.getProperty("nonexistingProperty");
    }

    @Test
    public void getProperties() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));

        BuildVariant buildVariant = new BuildVariant(variant);

        assertEquals(variant.getProperties(), buildVariant.getProperties());
        assertEquals(variant.getProperties(), buildVariant.getProperty("properties"));
    }

    @Test(expected = ReadOnlyPropertyException.class)
    public void setProperty() {
        Variant variant = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));

        BuildVariant buildVariant = new BuildVariant(variant);

        buildVariant.setProperty("systemVersion", "2.0");
    }

}
