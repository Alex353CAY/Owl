package owlFramework.Execution;

import owlFramework.Artifact;
import owlFramework.Exceptions.CyclicDependencyDetectedException;
import owlFramework.Exceptions.ElementNotFoundException;
import owlFramework.Plugin.PluginExecutionException;

import java.util.*;
import java.util.concurrent.*;

public class PluginRequest {
    protected final Artifact artifact;
    protected final Set<PluginRequest> dependencies = Collections.newSetFromMap(new ConcurrentHashMap<>());
    protected final Map<String, String> configuration = new ConcurrentHashMap<>();

    private volatile boolean isExecuted = false;
    private volatile ExecutionStatus status = ExecutionStatus.NotStarted;

    public PluginRequest(Artifact artifact) {
        this.artifact = artifact;
    }

    public PluginRequest(Artifact artifact, Map<String, String> configuration) {
        this(artifact);
        this.configuration.putAll(configuration);
    }

    protected void addDependency(PluginRequest request) throws CyclicDependencyDetectedException {
        try {
            while (isExecuted) {
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (request.dependsOn(this)) throw new CyclicDependencyDetectedException();
        dependencies.add(request);
    }

    public final synchronized void execute(IPluginManager pluginManager, ExecutorService service) {
        isExecuted = true;

        for (PluginRequest request: toSet()) {
            request.setStatus(ExecutionStatus.NotStarted);
        }

        Queue<PluginRequest> queue = new ConcurrentLinkedQueue<>();
        queue.addAll(toSet());
        while (!queue.isEmpty()) {
            PluginRequest dependency = queue.poll();
            if (dependency != null) {
                if (dependency.isExecutable()) {
                    service.submit(() -> {
                        dependency.execute(pluginManager);
                        return null;
                    });
                //} else if (dependency.status != ExecutionStatus.Finished)
                } else if (!dependency.isFinished())
                    queue.add(dependency);
            }
        }
        //System.out.println(isFinished());
    }

    public final synchronized void execute(Artifact artifact, IPluginManager pluginManager, ExecutorService service) {
        for (PluginRequest pluginRequest : toSet()) {
            if (pluginRequest.artifact.equals(artifact)) {
                pluginRequest.execute(pluginManager, service);
                break;
            }
        }
    }

    private synchronized void execute(IPluginManager pluginManager) throws ElementNotFoundException, PluginExecutionException {
        status = ExecutionStatus.Processing;
        executeArtifact(pluginManager);
        status = ExecutionStatus.Finished;
    }

    protected void executeArtifact(IPluginManager pluginManager) throws ElementNotFoundException, PluginExecutionException {
        pluginManager.get(artifact).execute(configuration);
    }

    private synchronized boolean isExecutable() {
        for (PluginRequest dependency : dependencies) {
            if (!dependency.isFinished()) return false;
        }
        return status == ExecutionStatus.NotStarted;
    }

    protected synchronized boolean isFinished() {
        return status == ExecutionStatus.Finished;
    }

    private synchronized boolean isProcessing() {
        return status == ExecutionStatus.Processing;
    }

    private synchronized void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    private Set<PluginRequest> toSet() {
        Set<PluginRequest> list = new LinkedHashSet<>();
        for (PluginRequest dependency: dependencies) {
            list.addAll(dependency.toSet());
            list.add(dependency);
        }
        list.add(this);
        return list;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PluginRequest) {
            PluginRequest o = (PluginRequest) obj;
            return artifact.equals(o.artifact) && configuration.equals(o.configuration);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31*31*artifact.hashCode() + 31*configuration.hashCode();
    }

    private boolean dependsOn(PluginRequest request) {
        for (PluginRequest dependency : dependencies) {
            if (dependency.equals(request) || dependency.dependsOn(request)) return true;
        }
        return false;
    }

    enum ExecutionStatus {
        NotStarted,
        Processing,
        Finished
    }
}
