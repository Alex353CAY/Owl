package OwlBuilder.Plugins.compilers;

import OwlBuilder.Plugins.MessageEvent;
import OwlBuilder.Plugins.Plugin;
import owlFramework.Plugin.PluginExecutionException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.util.Map;

public class objcopy extends Plugin {
    public objcopy(Path sourcePath, Path targetPath) {
        this("1.0", sourcePath, targetPath);
    }

    protected objcopy(String version, Path sourcePath, Path targetPath) {
        super("osdev", "objcopy", version, sourcePath, targetPath);
    }

    @Override
    public void run(Map<String, String> options) throws PluginExecutionException {
        try {
            for (Map.Entry<String, String> entry : options.entrySet()) {
                if (entry.getKey().startsWith("file")) {
                    String commandBuilder = "objcopy " + targetPath + '\\' + entry.getValue() + " -O binary";
                    ProcessBuilder procBuilder = new ProcessBuilder(commandBuilder.split(" "));
                    procBuilder.redirectErrorStream(true);

                    // запуск программы
                    Process process = procBuilder.start();

                    // читаем стандартный поток вывода
                    // и выводим на экран
                    InputStream stdout = process.getInputStream();
                    InputStreamReader isrStdout = new InputStreamReader(stdout);
                    BufferedReader brStdout = new BufferedReader(isrStdout);

                    String line = null;
                    while ((line = brStdout.readLine()) != null) {
                        notifyListeners(new MessageEvent(this, line));
                    }
                    int executionCode = process.waitFor();
                    if (executionCode != 0) throw new PluginExecutionException("objcopy finished with " + executionCode + " code");
                }
            }
        } catch (Exception e) {
            throw new PluginExecutionException(e);
        }
    }
}
