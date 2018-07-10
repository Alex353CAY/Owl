package owlFramework.Plugin.Event;

import owlFramework.Plugin.Plugin;

public class PluginFinishedEvent extends AbstractExecutionEvent {
    public PluginFinishedEvent(Plugin plugin) {
        super(plugin);
    }
}
