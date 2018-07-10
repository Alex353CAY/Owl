package owlFramework.Plugin.Event;

import owlFramework.Plugin.Plugin;

public class AbstractExecutionEvent implements PluginExecutionEvent {
    private final Plugin plugin;

    protected AbstractExecutionEvent(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
