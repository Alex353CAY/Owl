package owlFramework.Plugin;

import owlFramework.Plugin.Event.PluginExecutionEvent;

public interface PluginListener {
    void onExecutionEvent(PluginExecutionEvent event);
}
