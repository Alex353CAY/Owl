package owlFramework;

import owlFramework.Exceptions.ElementNotFoundException;

public interface Loader {
    Artifact load(Artifact artifact) throws ElementNotFoundException;
}
