package OwlBuilder;

import owlFramework.Artifact;
import owlFramework.Execution.IPluginManager;

import java.util.HashSet;
import java.util.concurrent.ExecutorService;

public class Project extends Artifact {
    private final PluginRequest request;

    public Project(String groupId, String artifactId, String version, PluginRequest request) {
        super(groupId, artifactId, version);
        this.request = request;
        //this.requests = new Phase[requests.size()];
        //requests.toArray(this.requests);
    }

    public void execute(Artifact artifact, IPluginManager pluginManager, ExecutorService service) {
        request.execute(artifact, pluginManager, service);
    }

    public void execute(IPluginManager pluginManager, ExecutorService service) {
        request.execute(pluginManager, service);
    }
}
