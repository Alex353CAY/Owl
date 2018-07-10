package owlFramework.Execution;

import owlFramework.Artifact;
import owlFramework.Exceptions.ElementNotFoundException;
import owlFramework.Plugin.Plugin;

public interface IPluginManager {
    Plugin get(Artifact plugin) throws ElementNotFoundException;
}
