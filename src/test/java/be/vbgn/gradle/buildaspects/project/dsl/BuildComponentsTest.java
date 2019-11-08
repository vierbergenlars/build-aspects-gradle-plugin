package be.vbgn.gradle.buildaspects.project.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.component.Component;
import groovy.lang.MissingPropertyException;
import groovy.lang.ReadOnlyPropertyException;
import java.util.Collections;
import org.junit.Test;

public class BuildComponentsTest {
    @Test
    public void getProperty() {
        Component component = TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"));

        BuildComponents buildComponents = new BuildComponents(component);

        assertEquals("1.0", buildComponents.getProperty("systemVersion"));
    }

    @Test(expected = MissingPropertyException.class)
    public void getPropertyNonExisting() {
        Component component = TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"));

        BuildComponents buildComponents = new BuildComponents(component);

        buildComponents.getProperty("nonexistingProperty");
    }

    @Test
    public void getProperties() {
        Component component = TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"));

        BuildComponents buildComponents = new BuildComponents(component);

        assertEquals(component.getProperties(), buildComponents.getProperties());
        assertEquals(component.getProperties(), buildComponents.getProperty("properties"));
    }

    @Test(expected = ReadOnlyPropertyException.class)
    public void setProperty() {
        Component component = TestUtil.createComponent(Collections.singletonMap("systemVersion", "1.0"));

        BuildComponents buildComponents = new BuildComponents(component);

        buildComponents.setProperty("systemVersion", "2.0");
    }

}
