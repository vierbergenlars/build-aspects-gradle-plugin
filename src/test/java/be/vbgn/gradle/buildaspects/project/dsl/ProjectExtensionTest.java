package be.vbgn.gradle.buildaspects.project.dsl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import be.vbgn.gradle.buildaspects.TestUtil;
import be.vbgn.gradle.buildaspects.project.project.VariantProject;
import be.vbgn.gradle.buildaspects.project.project.VariantProjectFactory;
import be.vbgn.gradle.buildaspects.variant.Variant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.gradle.api.Project;
import org.gradle.api.UnknownProjectException;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.Test;

public class ProjectExtensionTest {

    private static class VariantProjectFactoryMock implements VariantProjectFactory {

        private Map<Project, ? extends Variant> projectVariants;

        private VariantProjectFactoryMock(Map<Project, ? extends Variant> projectVariants) {
            this.projectVariants = projectVariants;
        }

        @Override
        public Optional<VariantProject> createVariantProject(Project project) {
            return Optional.ofNullable(projectVariants.get(project))
                    .map(variant -> new VariantProject() {
                        @Override
                        public Project getProject() {
                            return project;
                        }

                        @Override
                        public Variant getVariant() {
                            return variant;
                        }
                    });
        }

        @Override
        public Set<VariantProject> createVariantProjectsForParent(Project parentProject) {
            return parentProject.getChildProjects()
                    .values()
                    .stream()
                    .flatMap(project -> createVariantProject(project)
                            .map(Stream::of)
                            .orElseGet(Stream::empty))
                    .collect(Collectors.toSet());
        }
    }

    @Test
    public void findProject() {
        Variant version1 = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));
        Variant version2 = TestUtil.createVariant(Collections.singletonMap("systemVersion", "2.0"));
        Project rootProject = ProjectBuilder.builder().build();
        Project moduleA = ProjectBuilder.builder().withName("moduleA").withParent(rootProject).build();
        Project moduleA1 = ProjectBuilder.builder().withName("moduleA-1").withParent(moduleA).build();
        Project moduleA2 = ProjectBuilder.builder().withName("moduleA-2").withParent(moduleA).build();
        Project moduleB = ProjectBuilder.builder().withName("moduleB").withParent(rootProject).build();
        Project moduleB1 = ProjectBuilder.builder().withName("moduleB-1").withParent(moduleB).build();
        Project moduleB2 = ProjectBuilder.builder().withName("moduleB-2").withParent(moduleB).build();
        Map<Project, Variant> projectVariantMap = new HashMap<>();
        projectVariantMap.put(moduleA1, version1);
        projectVariantMap.put(moduleA2, version2);
        projectVariantMap.put(moduleB1, version1);
        projectVariantMap.put(moduleB2, version2);

        VariantProjectFactory variantProjectFactory = new VariantProjectFactoryMock(projectVariantMap);
        ProjectExtension projectExtension = new ProjectExtension(rootProject, variantProjectFactory);

        Project moduleA1Project = projectExtension.findProject(":moduleA", version1);
        assertNotNull(moduleA1Project);
        assertEquals(moduleA1, moduleA1Project);

        Project moduleA2Project = projectExtension.findProject(":moduleA", version2);
        assertNotNull(moduleA2Project);
        assertEquals(moduleA2, moduleA2Project);

        Project moduleA3Project = projectExtension
                .findProject(":moduleA", TestUtil.createVariant(Collections.singletonMap("systemVersion", "3.0")));
        assertNull(moduleA3Project);
    }

    @Test(expected = IllegalStateException.class)
    public void findProjectIllegalState() {
        Variant version1 = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));
        Variant version2 = TestUtil.createVariant(Collections.singletonMap("systemVersion", "2.0"));
        Project rootProject = ProjectBuilder.builder().build();
        Project moduleA = ProjectBuilder.builder().withName("moduleA").withParent(rootProject).build();
        Project moduleA1 = ProjectBuilder.builder().withName("moduleA-1").withParent(moduleA).build();
        Project moduleA2 = ProjectBuilder.builder().withName("moduleA-2").withParent(moduleA).build();
        Project moduleB = ProjectBuilder.builder().withName("moduleB").withParent(rootProject).build();
        Project moduleB1 = ProjectBuilder.builder().withName("moduleB-1").withParent(moduleB).build();
        Project moduleB1bis = ProjectBuilder.builder().withName("moduleB-1bis").withParent(moduleB).build();
        Project moduleB2 = ProjectBuilder.builder().withName("moduleB-2").withParent(moduleB).build();
        Map<Project, Variant> projectVariantMap = new HashMap<>();
        projectVariantMap.put(moduleA1, version1);
        projectVariantMap.put(moduleA2, version2);
        projectVariantMap.put(moduleB1, version1);
        projectVariantMap.put(moduleB1bis, version1);
        projectVariantMap.put(moduleB2, version2);

        VariantProjectFactory variantProjectFactory = new VariantProjectFactoryMock(projectVariantMap);
        ProjectExtension projectExtension = new ProjectExtension(rootProject, variantProjectFactory);

        projectExtension.findProject(":moduleB", version1);
    }

    @Test
    public void project() {
        Variant version1 = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));
        Variant version2 = TestUtil.createVariant(Collections.singletonMap("systemVersion", "2.0"));
        Project rootProject = ProjectBuilder.builder().build();
        Project moduleA = ProjectBuilder.builder().withName("moduleA").withParent(rootProject).build();
        Project moduleA1 = ProjectBuilder.builder().withName("moduleA-1").withParent(moduleA).build();
        Project moduleA2 = ProjectBuilder.builder().withName("moduleA-2").withParent(moduleA).build();
        Project moduleB = ProjectBuilder.builder().withName("moduleB").withParent(rootProject).build();
        Project moduleB1 = ProjectBuilder.builder().withName("moduleB-1").withParent(moduleB).build();
        Project moduleB2 = ProjectBuilder.builder().withName("moduleB-2").withParent(moduleB).build();
        Map<Project, Variant> projectVariantMap = new HashMap<>();
        projectVariantMap.put(moduleA1, version1);
        projectVariantMap.put(moduleA2, version2);
        projectVariantMap.put(moduleB1, version1);
        projectVariantMap.put(moduleB2, version2);

        VariantProjectFactory variantProjectFactory = new VariantProjectFactoryMock(projectVariantMap);
        ProjectExtension projectExtension = new ProjectExtension(rootProject, variantProjectFactory);

        Project moduleA1Project = projectExtension
                .project(":moduleA", TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0")));
        assertEquals(moduleA1, moduleA1Project);
    }

    @Test(expected = UnknownProjectException.class)
    public void projectNotPresent() {
        VariantProjectFactory variantProjectFactory = new VariantProjectFactoryMock(Collections.emptyMap());
        Project rootProject = ProjectBuilder.builder().build();
        ProjectExtension projectExtension = new ProjectExtension(rootProject, variantProjectFactory);

        projectExtension.project(":moduleA", TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0")));
    }

    @Test
    public void variantTask() {
        Variant version1 = TestUtil.createVariant(Collections.singletonMap("systemVersion", "1.0"));
        Variant version2 = TestUtil.createVariant(Collections.singletonMap("systemVersion", "2.0"));
        Project rootProject = ProjectBuilder.builder().build();
        Project moduleA = ProjectBuilder.builder().withName("moduleA").withParent(rootProject).build();
        Project moduleA1 = ProjectBuilder.builder().withName("moduleA-1").withParent(moduleA).build();
        Project moduleA2 = ProjectBuilder.builder().withName("moduleA-2").withParent(moduleA).build();
        Project moduleB = ProjectBuilder.builder().withName("moduleB").withParent(rootProject).build();
        Project moduleB1 = ProjectBuilder.builder().withName("moduleB-1").withParent(moduleB).build();
        Project moduleB2 = ProjectBuilder.builder().withName("moduleB-2").withParent(moduleB).build();
        Map<Project, Variant> projectVariantMap = new HashMap<>();
        projectVariantMap.put(moduleA1, version1);
        projectVariantMap.put(moduleA2, version2);
        projectVariantMap.put(moduleB1, version1);
        projectVariantMap.put(moduleB2, version2);

        VariantProjectFactory variantProjectFactory = new VariantProjectFactoryMock(projectVariantMap);
        ProjectExtension projectExtension = new ProjectExtension(rootProject, variantProjectFactory);

        assertEquals(":moduleA:moduleA-1:test", projectExtension.variantTask(":moduleA", version1, "test"));
    }

}
