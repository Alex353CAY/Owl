package owlFramework.Plugin;

public class PluginExecutionException extends Exception {
    public PluginExecutionException(Exception cause) {
        super(cause);
    }
    public PluginExecutionException(String message) {
        super(message);
    }
}
