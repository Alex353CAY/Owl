package owlFramework.Execution;

import owlFramework.Artifact;
import owlFramework.Exceptions.ElementNotFoundException;
import owlFramework.Loader;
import owlFramework.Plugin.Plugin;

import java.util.HashSet;
import java.util.Set;

public class PluginManager implements IPluginManager {
    private final Loader loader;
    private final Set<Plugin> plugins = new HashSet<>();

    public PluginManager(Loader loader) {
        this.loader = loader;
    }

    public void add(Plugin plugin) {
        plugins.add(plugin);
    }

    @Override
    public Plugin get(Artifact plugin) throws ElementNotFoundException {
        for (Plugin p : plugins) {
            if (p.equals(plugin))
                return p;
        }
        return (Plugin) loader.load(plugin);
    }
}
