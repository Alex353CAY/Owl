package owlFramework;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import owlFramework.Exceptions.CyclicDependencyDetectedException;
import owlFramework.Exceptions.DublicatingElementException;
import owlFramework.Exceptions.ElementNotFoundException;
import owlFramework.Execution.PluginManager;
import owlFramework.Execution.PluginRequest;
import owlFramework.Plugin.Event.PluginExecutionEvent;
import owlFramework.Plugin.Event.PluginFinishedEvent;
import owlFramework.Plugin.Event.PluginStartedEvent;
import owlFramework.Plugin.PluginListener;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PluginGraphTest {

    private static Artifact rootArtifact = new Artifact("test", "root", "1.0");
    private static Artifact gccArtifact = new Artifact("test", "gcc", "1.0");
    private static Artifact objcopyArtifact = new Artifact("test", "objcopy", "1.0");
    private static Artifact nasmArtifact = new Artifact("test", "nasm", "1.0");
    private static Artifact packerArtifact = new Artifact("test", "packager", "1.0");

    @Test(expected = CyclicDependencyDetectedException.class)
    public void cyclicDependencyDetectionTest() throws CyclicDependencyDetectedException {
        PluginLoader loader = new PluginLoader();
        PluginRequest root = new PluginRequest(rootArtifact);
        PluginRequest gcc = new PluginRequest(gccArtifact);
        PluginRequest objcopy = new PluginRequest(objcopyArtifact);
        PluginRequest nasm = new PluginRequest(nasmArtifact);
        PluginRequest packer = new PluginRequest(packerArtifact);

        root.addDependency(packer);
        packer.addDependency(objcopy);
        packer.addDependency(nasm);
        objcopy.addDependency(packer);
        packer.addDependency(gcc);
    }

    private void run(int threads) throws CyclicDependencyDetectedException, InterruptedException {
        PluginLoader loader = new PluginLoader();
        PluginRequest root = new PluginRequest(rootArtifact);
        PluginRequest gcc = new PluginRequest(gccArtifact);
        PluginRequest objcopy = new PluginRequest(objcopyArtifact);
        PluginRequest nasm = new PluginRequest(nasmArtifact);
        PluginRequest packer = new PluginRequest(packerArtifact);

        PluginListener listener = new PluginListener() {
            private volatile boolean nasmFinished = false;
            private volatile boolean gccFinished = false;
            private volatile boolean objCopyFinished = false;

            @Override
            public synchronized void onExecutionEvent(PluginExecutionEvent event) {
                if (event instanceof PluginStartedEvent) {
                    switch (event.getPlugin().artifactId) {
                        case "packager": {
                            if (!(nasmFinished && gccFinished && objCopyFinished)) System.out.println("ERROR");
                            break;
                        }
                        case "objcopy": {
                            if (!gccFinished) System.out.println("ERROR");
                            break;
                        }
                    }
                } else if (event instanceof PluginFinishedEvent) {
                    switch (event.getPlugin().artifactId) {
                        case "nasm": {
                            nasmFinished = true;
                            break;
                        }
                        case "gcc": {
                            gccFinished = true;
                            break;
                        }
                        case "objcopy": {
                            objCopyFinished = true;
                            break;
                        }
                    }
                }
            }
        };

        root.addDependency(packer);
        packer.addDependency(objcopy);
        packer.addDependency(nasm);
        objcopy.addDependency(gcc);
        packer.addDependency(gcc);

        PluginManager manager = new PluginManager(null);
        Plugin rootPlugin = new Plugin(rootArtifact);
        Plugin gccPlugin = new Plugin(gccArtifact);
        Plugin objcopyPlugin = new Plugin(objcopyArtifact);
        Plugin nasmPlugin = new Plugin(nasmArtifact);
        Plugin packerPlugin = new Plugin(packerArtifact);

        rootPlugin.addListener(listener);
        gccPlugin.addListener(listener);
        objcopyPlugin.addListener(listener);
        nasmPlugin.addListener(listener);
        packerPlugin.addListener(listener);


        manager.add(rootPlugin);
        manager.add(gccPlugin);
        manager.add(objcopyPlugin);
        manager.add(nasmPlugin);
        manager.add(packerPlugin);

        manager.add(rootPlugin);
        manager.add(gccPlugin);
        manager.add(objcopyPlugin);
        manager.add(nasmPlugin);
        manager.add(packerPlugin);
        ExecutorService service = Executors.newFixedThreadPool(threads);
        packer.execute(manager, service);
    }

    @Test
    public void oneThreadTest() throws ElementNotFoundException, CyclicDependencyDetectedException, DublicatingElementException, InterruptedException {
        run(1);
    }

    @Test
    public void thousandThreadsTest() throws ElementNotFoundException, CyclicDependencyDetectedException, DublicatingElementException, InterruptedException {
        run(1000);
    }

    private class PluginRequest extends owlFramework.Execution.PluginRequest {
        public PluginRequest(Artifact artifact) {
            super(artifact);
        }

        public void addDependency(PluginRequest dependency) throws CyclicDependencyDetectedException {
            super.addDependency(dependency);
        }
    }

    private class PluginLoader implements Loader {
        public Plugin[] plugins = {
                new Plugin("test", "gcc", "1.0"),
                new Plugin("test", "nasm", "1.0"),
                new Plugin("test", "objcopy", "1.0"),
                new Plugin("test", "packager", "1.0"),
        };

        @Override
        public Artifact load(Artifact artifact) throws ElementNotFoundException {
            for (owlFramework.Plugin.Plugin plugin : plugins) {
                if (artifact.equals(plugin)) return plugin;
            }
            throw new ElementNotFoundException();
        }
    }

    private class Plugin extends owlFramework.Plugin.Plugin {
        public Plugin(Artifact artifact) {
            super(artifact.groupId, artifact.artifactId, artifact.version);
        }
        public Plugin(String groupId, String artifactId, String version) { super(groupId, artifactId, version); }

        @Override
        public void run(Map<String, String> options) {
            try {
                if (artifactId.equals("nasm")) Thread.sleep(2000);
                if (artifactId.equals("gcc")) Thread.sleep(2000);
            } catch (Exception e) {}
            System.out.println(groupId + ' ' + artifactId + ' ' + version);
        }
    }
}
