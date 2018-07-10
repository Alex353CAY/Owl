package OwlBuilder.Plugins.compilers;

import OwlBuilder.Plugins.MessageEvent;
import OwlBuilder.Plugins.Plugin;
import owlFramework.Plugin.PluginExecutionException;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Map;

public class gcc extends Plugin {
    public gcc(Path sourcePath, Path targetPath) {
        this("1.0", sourcePath, targetPath);
    }

    protected gcc(String version, Path sourcePath, Path targetPath) {
        super("compilers.gcc", "gcc", version, sourcePath, targetPath);
    }

    @Override
    public void run(Map<String, String> options) throws PluginExecutionException {
        try {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    String filePathStr = file.toString();
                    String extension = filePathStr.substring(filePathStr.lastIndexOf('.'));
                    if (!extension.equals(".c") && !extension.equals(".h")) return FileVisitResult.CONTINUE;
                    File output = new File(targetPath + "\\" + artifactId);
                    output.mkdirs();
                    String targetFile = (output.toPath() + "\\" + sourcePath.relativize(file));
                    String commandBuilder = "gcc " + options.get("args") + ' ' + file + " -o " + targetFile.substring(0, targetFile.lastIndexOf('.')) + ".bin";
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
                    while((line = brStdout.readLine()) != null) {
                        if (line.contains("error")) notifyListeners(new MessageEvent(gcc.this, line));
                    }
                    int executionCode;
                    try {
                        executionCode = process.waitFor();
                    } catch (InterruptedException e) {
                        return FileVisitResult.TERMINATE;
                    }
                    if (executionCode == 0) {
                        return FileVisitResult.CONTINUE;
                    }
                    else return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new PluginExecutionException(e);
        }
    }
}
