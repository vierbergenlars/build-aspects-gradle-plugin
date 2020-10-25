package be.vbgn.gradle.buildaspects.fixtures;

import java.io.File;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.gradle.api.initialization.ProjectDescriptor;

public class ProjectDescriptorFixture implements ProjectDescriptor {

    private final String path;
    private String name;
    private final ProjectDescriptor parent;
    private final SettingsFixture settings;

    ProjectDescriptorFixture(String path, String name, ProjectDescriptor parent, SettingsFixture settings) {
        this.path = path;
        this.name = name;
        this.parent = parent;
        this.settings = settings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ProjectDescriptorFixture that = (ProjectDescriptorFixture) o;
        return getPath().equals(that.getPath()) &&
                settings.equals(that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPath(), settings);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String s) {
        this.name = s;

    }

    @Override
    public File getProjectDir() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setProjectDir(File file) {

        throw new UnsupportedOperationException();
    }

    @Override
    public String getBuildFileName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setBuildFileName(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public File getBuildFile() {
        throw new UnsupportedOperationException();
    }

    @Nullable
    @Override
    public ProjectDescriptor getParent() {
        return parent;
    }

    @Override
    public Set<ProjectDescriptor> getChildren() {
        return settings
                .projectDescriptorSet.stream()
                .filter(projectDescriptor -> Objects.equals(projectDescriptor.getParent(), this))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPath() {
        return path;
    }
}
