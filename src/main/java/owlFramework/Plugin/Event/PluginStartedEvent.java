package owlFramework.Plugin.Event;

import owlFramework.Plugin.Plugin;

public class PluginStartedEvent extends AbstractExecutionEvent {
    public PluginStartedEvent(Plugin plugin) {
        super(plugin);
    }
}
