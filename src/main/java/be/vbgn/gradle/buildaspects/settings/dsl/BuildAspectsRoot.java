package be.vbgn.gradle.buildaspects.settings.dsl;

import org.gradle.api.Action;

public interface BuildAspectsRoot extends BuildAspects {

    void nested(Action<? super BuildAspects> action);
}
