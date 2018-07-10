package OwlBuilder.XML;

import OwlBuilder.Phase;
import OwlBuilder.PluginRequest;
import OwlBuilder.Project;
import OwlBuilder.XML.Exceptions.IllegalArtifactIdException;
import OwlBuilder.XML.Exceptions.IllegalGroupIdException;
import OwlBuilder.XML.Exceptions.IllegalVersionException;
import OwlBuilder.XML.Exceptions.PhaseNameConflictException;

import java.util.HashSet;

public class ProjectBuilder {
    private String groupId;
    private String artifactId;
    private String version;
    private final HashSet<PluginRequest> plugins = new HashSet<>();

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public synchronized void addPlugin(PluginRequest pluginRequest) throws PhaseNameConflictException {
        if (plugins.contains(pluginRequest)) throw new PhaseNameConflictException();
        plugins.add(pluginRequest);
    }

    public void reset() {
        groupId = null;
        artifactId = null;
        version = null;
    }

    public Project build() throws IllegalGroupIdException, IllegalArtifactIdException, IllegalVersionException {
        if (groupId == null) throw new IllegalGroupIdException();
        if (artifactId == null) throw new IllegalArtifactIdException();
        if (version == null) throw new IllegalVersionException();

        return new Project(groupId, artifactId, version, plugins.iterator().next());
    }
}
