package owlFramework.Plugin;

import owlFramework.Artifact;
import owlFramework.Plugin.Event.PluginExecutionEvent;
import owlFramework.Plugin.Event.PluginFinishedEvent;
import owlFramework.Plugin.Event.PluginStartedEvent;

import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Plugin extends Artifact {
    private final Notifier notifier = new Notifier(this);

    public Plugin(String groupId, String artifactId, String version) {
        super(groupId, artifactId, version);
    }

    public void addListener(PluginListener listener) {
        notifier.addListener(listener);
    }

    public final void execute(Map<String, String> options) throws PluginExecutionException {
        Thread notifierThread = new Thread(this.notifier);
        notifierThread.start();
        try {
            notifyListeners(new PluginStartedEvent(this));
            run(options);
            notifyListeners(new PluginFinishedEvent(this));
        } finally {
            notifier.interrupt();
        }
    }

    public abstract void run(Map<String, String> options) throws PluginExecutionException;

    protected final void notifyListeners(PluginExecutionEvent event) {
        notifier.notifyListeners(event);
    }

    protected final class Notifier implements Runnable {
        private volatile boolean interrupted = false;
        private Plugin plugin;
        private final Queue<PluginExecutionEvent> events = new ArrayDeque<>();
        private final Set<PluginListener> listeners = new CopyOnWriteArraySet<>();

        Notifier(Plugin plugin) {
            this.plugin = plugin;
        }

        void addListener(PluginListener listener) {
            listeners.add(listener);
        }

        @Override
        public void run() {
            while (!interrupted) {
                while (!events.isEmpty()) {
                    for (PluginListener listener : listeners) {
                        listener.onExecutionEvent(events.peek());
                    }
                    events.remove();
                }
            }
        }

        void notifyListeners(PluginExecutionEvent event) {
            events.add(event);
        }

        public void interrupt() {
            interrupted = true;
        }
    }
}
