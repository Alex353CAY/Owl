package OwlBuilder.Plugins;

import java.nio.file.Path;

public abstract class Plugin extends owlFramework.Plugin.Plugin {
    protected final Path sourcePath, targetPath;

    public Plugin(String groupId, String artifactId, String version, Path sourcePath, Path targetPath) {
        super(groupId, artifactId, version);
        this.sourcePath = sourcePath;
        this.targetPath = targetPath;
    }
}
