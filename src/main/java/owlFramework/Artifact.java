package owlFramework;

public class Artifact {
    public final String groupId;
    public final String artifactId;
    public final String version;

    public Artifact(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof Artifact) {
            Artifact o = (Artifact) obj;
            return groupId.equals(o.groupId) && artifactId.equals(o.artifactId) && version.equals(o.version);
        }
        return false;
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
