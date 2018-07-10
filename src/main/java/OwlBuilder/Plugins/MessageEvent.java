package OwlBuilder.Plugins;

import owlFramework.Plugin.Event.PluginExecutionEvent;
import owlFramework.Plugin.Plugin;

public class MessageEvent implements PluginExecutionEvent {
    private OwlBuilder.Plugins.Plugin plugin;
    public final String message;

    public MessageEvent(OwlBuilder.Plugins.Plugin plugin, String message) {
        this.plugin = plugin;
        this.message = message;
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }
}
