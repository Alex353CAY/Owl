package OwlBuilder;

import owlFramework.Artifact;
import owlFramework.Exceptions.CyclicDependencyDetectedException;

import java.util.Map;

public class PluginRequest extends owlFramework.Execution.PluginRequest {
    public PluginRequest(Artifact artifact) {
        super(artifact);
    }

    public PluginRequest(Artifact artifact, Map<String, String> configuration) {
        super(artifact, configuration);
    }

    @Override
    public void addDependency(owlFramework.Execution.PluginRequest request) throws CyclicDependencyDetectedException {
        super.addDependency(request);
    }

    @Override
    public synchronized boolean isFinished() {
        return super.isFinished();
    }

    public Artifact getArtifact() {
        return artifact;
    }
}
