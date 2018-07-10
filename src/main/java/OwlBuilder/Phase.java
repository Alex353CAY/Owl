package OwlBuilder;

import owlFramework.Artifact;
import owlFramework.Exceptions.CyclicDependencyDetectedException;
import owlFramework.Execution.IPluginManager;

public class Phase extends PluginRequest {
    public Phase(String name) {
        super(new Artifact("phase", name, ""));
    }

    public void addDependency(Phase phase) throws CyclicDependencyDetectedException {
        super.addDependency(phase);
    }

    public void addPlugin(PluginRequest pluginRequest) throws CyclicDependencyDetectedException {
        /*for (owlFramework.Execution.PluginRequest dependency: dependencies) {
            if (dependency instanceof Phase) {
                pluginRequest.addDependency(dependency);
            }
        }*/
        addDependency(pluginRequest);
    }

    @Override
    protected void executeArtifact(IPluginManager pluginManager) {}

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Phase) {
            return super.equals(obj);
        }
        return false;
    }
}
