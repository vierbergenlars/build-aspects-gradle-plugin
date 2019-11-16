package be.vbgn.gradle.buildaspects.settings.dsl;

import be.vbgn.gradle.buildaspects.aspect.AspectHandler;
import be.vbgn.gradle.buildaspects.settings.project.DuplicateProjectException;
import be.vbgn.gradle.buildaspects.settings.project.ParentVariantProjectDescriptor;
import be.vbgn.gradle.buildaspects.settings.project.ProjectHandler;
import be.vbgn.gradle.buildaspects.settings.project.VariantProjectDescriptor;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.Namer;
import org.gradle.api.initialization.Settings;
import org.gradle.api.model.ObjectFactory;

public class BuildAspectsRoot implements BuildAspects {

    private BuildAspects rootBuildAspect = null;
    private final Set<BuildAspects> allBuildAspects = new HashSet<>();
    private final Set<String> registeredProjects = new HashSet<>();
    private final Supplier<BuildAspects> buildAspectsFactory;

    @Inject
    public BuildAspectsRoot(ObjectFactory objectFactory, Settings settings) {
        this(() -> objectFactory.newInstance(BuildAspectsImpl.class, settings));
    }

    BuildAspectsRoot(Supplier<BuildAspects> buildAspectsFactory) {
        this.buildAspectsFactory = buildAspectsFactory;
    }

    private BuildAspects createBuildAspects() {
        if (rootBuildAspect != null) {
            throw IllegalBuildAspectsStateException.nestedAndRootConfiguration();
        }
        return createBuildAspectsUnchecked();
    }

    private BuildAspects createBuildAspectsUnchecked() {
        BuildAspects buildAspects = buildAspectsFactory.get();
        allBuildAspects.add(buildAspects);
        buildAspects.getProjects().projectAdded(projectDescriptor -> {
            if (!registeredProjects.add(projectDescriptor.getPath())) {
                // Project has already been registered before
                throw new DuplicateProjectException(
                        "The project has already been registered in an other buildAspects configuration.",
                        DuplicateProjectException.forProject(projectDescriptor));
            }
        });
        return buildAspects;
    }

    private BuildAspects createRootBuildAspects() {
        if (rootBuildAspect == null) {
            if (!allBuildAspects.isEmpty()) {
                throw IllegalBuildAspectsStateException.nestedAndRootConfiguration();
            }
            rootBuildAspect = createBuildAspects();
        }
        return rootBuildAspect;
    }

    public void nested(Action<? super BuildAspects> action) {
        action.execute(createBuildAspects());
    }

    @Override
    public AspectHandler getAspects() {
        return createRootBuildAspects().getAspects();
    }

    @Override
    public ProjectHandler getProjects() {
        return createRootBuildAspects().getProjects();
    }

    @Override
    public void setProjectNamer(Namer<ParentVariantProjectDescriptor> namer) {
        createRootBuildAspects().setProjectNamer(namer);
    }

    @Override
    public Set<VariantProjectDescriptor> getVariantProjects() {
        return Collections.unmodifiableSet(new LazyVariantProjectSet(allBuildAspects));
    }

    private static class LazyVariantProjectSet extends AbstractSet<VariantProjectDescriptor> {

        private final Set<? extends BuildAspects> buildAspects;

        public LazyVariantProjectSet(Set<? extends BuildAspects> buildAspects) {
            this.buildAspects = buildAspects;
        }

        private Stream<VariantProjectDescriptor> createProjectDescriptorStream() {
            return buildAspects.stream()
                    .map(BuildAspects::getVariantProjects)
                    .flatMap(Collection::stream);
        }

        @Override
        public Iterator<VariantProjectDescriptor> iterator() {
            return createProjectDescriptorStream().iterator();
        }

        @Override
        public int size() {
            return (int) createProjectDescriptorStream().count();
        }
    }
}
