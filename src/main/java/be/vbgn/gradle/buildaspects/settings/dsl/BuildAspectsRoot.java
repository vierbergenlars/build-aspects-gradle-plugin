package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.internal.LazySet;
import be.vbgn.gradle.buildaspects.settings.project.ParentVariantProjectDescriptor;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptor;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Namer;
import org.gradle.api.initialization.ProjectDescriptor;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;

public class BuildAspectsRoot implements BuildAspects {

    private final ObjectFactory objectFactory;
    private final Settings settings;
    private final BuildAspects rootBuildAspect;
    private final Set<BuildAspects> allBuildAspects = new HashSet<>();
    private final Set<ProjectDescriptor> registeredProjects = new HashSet<>();

    @Inject
    public BuildAspectsRoot(ObjectFactory objectFactory, Settings settings) {
        this.objectFactory = objectFactory;
        this.settings = settings;
        rootBuildAspect = createBuildAspects();
    }

    private BuildAspects createBuildAspects() {
        BuildAspects buildAspects = objectFactory.newInstance(BuildAspectsImpl.class, settings);
        allBuildAspects.add(buildAspects);
        buildAspects.getProjects().projectAdded(projectDescriptor -> {
            if (!registeredProjects.add(projectDescriptor)) {
                // Project has already been registered before
                throw new IllegalArgumentException("The project " + projectDescriptor.getPath()
                        + " has already been registered in an other buildAspects configuration.");
            }
        });
        return buildAspects;
    }

    public void nested(Action<? super BuildAspects> action) {
        action.execute(createBuildAspects());
    }

    @Override
    public AspectHandler getAspects() {
        return rootBuildAspect.getAspects();
    }

    @Override
    public ProjectHandler getProjects() {
        return rootBuildAspect.getProjects();
    }

    @Override
    public void setProjectNamer(Namer<ParentVariantProjectDescriptor> namer) {
        rootBuildAspect.setProjectNamer(namer);
    }

    @Override
    public Set<VariantProjectDescriptor> getVariantProjects() {
        Set<Set<VariantProjectDescriptor>> variantProjectDescriptors = allBuildAspects.stream()
                .map(BuildAspects::getVariantProjects)
                .collect(Collectors.toSet());
        return new LazySet<>(variantProjectDescriptors);
    }
}
