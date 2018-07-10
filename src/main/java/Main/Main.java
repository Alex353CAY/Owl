package Main;

import OwlBuilder.Plugins.MessageEvent;
import OwlBuilder.Plugins.compilers.gcc;
import OwlBuilder.Plugins.compilers.nasm;
import OwlBuilder.Plugins.compilers.objcopy;
import OwlBuilder.Plugins.osDev.packager;
import OwlBuilder.Project;
import OwlBuilder.XML.StAX.Parser;
import owlFramework.Artifact;
import owlFramework.Exceptions.ElementNotFoundException;
import owlFramework.Execution.PluginManager;
import owlFramework.Plugin.PluginListener;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("There is not goal to perform. Specify it as a launching argument.");
            return;
        }
        switch (args[0]) {
            case "run": {
                try {
                    Parser parser = new Parser();
                    String executionPath = System.getProperty("user.dir") + "\\build.xml";
                    if (!Files.exists(Paths.get(executionPath))) {
                        System.out.println("Current folder does not contain file named 'build.xml'. Aborting program.");
                        return;
                    }
                    Project project = parser.parseDocument(executionPath);
                    ExecutorService service = Executors.newFixedThreadPool(1);
                    project.execute(new PluginManager(new Loader()), service);
                    service.shutdown();
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                    try {
                        Thread.sleep(5);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    System.out.println("Something went wrong! It would be great if you'd send it to the developer (https://github.com/Alex353CAY).");
                    break;
                }
            }
            case "dependencies": {
                System.out.println("Sorry, this feature is not currently available, it will be added in the future.");
                break;
            }
            default: {
                System.out.println("Sorry, this feature is unknown to us, it is possible to add it in the future updates. Please contact the developer (https://github.com/Alex353CAY).");
                break;
            }
        }
    }

    private static class Loader implements owlFramework.Loader {
        private static PluginListener listener = event -> {
            if (event instanceof MessageEvent)
                System.out.println(((MessageEvent) event).message);
        };

        @Override
        public Artifact load(Artifact artifact) throws ElementNotFoundException {
            if (artifact.groupId.equals("compilers.gcc") && artifact.artifactId.equals("gcc")) {
                OwlBuilder.Plugins.Plugin gcc = new gcc(Paths.get("C:\\Users\\Alex353\\Documents\\Project\\src"), Paths.get("C:\\Users\\Alex353\\Documents\\Project\\target"));
                gcc.addListener(listener);
                return gcc;
            }
            if (artifact.groupId.equals("compilers.nasm") && artifact.artifactId.equals("nasm")) {
                OwlBuilder.Plugins.Plugin nasm = new nasm(Paths.get("C:\\Users\\Alex353\\Documents\\Project\\src"), Paths.get("C:\\Users\\Alex353\\Documents\\Project\\target"));
                nasm.addListener(listener);
                return nasm;
            }
            if (artifact.groupId.equals("compilers.gcc") && artifact.artifactId.equals("objcopy")) {
                OwlBuilder.Plugins.Plugin objcopy = new objcopy(Paths.get("C:\\Users\\Alex353\\Documents\\Project\\src"), Paths.get("C:\\Users\\Alex353\\Documents\\Project\\target"));
                objcopy.addListener(listener);
                return objcopy;
            }
            if (artifact.groupId.equals("osdev") && artifact.artifactId.equals("packager")) return new packager(Paths.get("C:\\Users\\Alex353\\Documents\\Project\\src"), Paths.get("C:\\Users\\Alex353\\Documents\\Project\\target"));
            return new Plugin(artifact);
        }
    }

    private static class Plugin extends owlFramework.Plugin.Plugin {
        public Plugin(Artifact artifact) {
            super(artifact.groupId, artifact.artifactId, artifact.version);
        }

        public Plugin(String groupId, String artifactId, String version) {
            super(groupId, artifactId, version);
        }

        @Override
        public void run(Map<String, String> options) {
            try {
                if (artifactId.equals("cleaner")) {
                    Thread.sleep(1000);
                }
                if (artifactId.equals("gcc")) {
                    Thread.sleep(2000);
                }
            } catch (Exception ignored) {}
            System.out.println(groupId + ' ' + artifactId + ' ' + version);
        }
    }
}
