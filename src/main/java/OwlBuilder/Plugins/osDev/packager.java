package OwlBuilder.Plugins.osDev;

import OwlBuilder.Plugins.Plugin;
import owlFramework.Plugin.PluginExecutionException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Map;

public class packager extends Plugin {
    public packager(Path sourcePath, Path targetPath) {
        this("1.0", sourcePath, targetPath);
    }

    protected packager(String version, Path sourcePath, Path targetPath) {
        super("osdev", "packager", version, sourcePath, targetPath);
    }

    @Override
    public void run(Map<String, String> options) throws PluginExecutionException {
        try {
            int partitionSize = (options.get("partitionSize") != null)?(Integer.parseInt(options.get("partitionSize"))):(512);
            final ArrayList<Partition> partitions = new ArrayList<>();
            String root = targetPath + "\\";
            String target = targetPath + "\\osdev\\" + options.get("output");
            for (Map.Entry<String, String> entry: options.entrySet()) {
                String key = entry.getKey();
                if (key.startsWith("partition") && !key.contains("Size")) {
                    int i = Integer.parseInt(key.substring(key.lastIndexOf("partition") + "partition".length()));
                    if (i > 1) {
                        Partition partition = new Partition(i, partitionSize, Paths.get(root + entry.getValue()));
                        partition.burn(Paths.get(target));
                    } else if (i == 1) {
                        Partition partition = new FirstPartition(partitionSize, Paths.get(root + entry.getValue()));
                        partition.burn(Paths.get(target));
                    }
                }
            }
        } catch (Exception e) {
            throw new PluginExecutionException(e);
        }
    }

    private class Partition {
        private final int index, partitionSize, contentLength;
        protected final Path content;

        Partition(int index, int partitionSize, Path content) {
            this(index, partitionSize, content, -1);
        }

        protected Partition(int index, int partitionSize, Path content, int contentLength) {
            this.partitionSize = partitionSize;
            this.index = index;
            this.contentLength = contentLength;
            this.content = content;
        }

        void burn(Path file) throws IOException {
            RandomAccessFile randomAccessFile = burnWithoutClosing(file, content);
            randomAccessFile.close();
        }

        protected RandomAccessFile burnWithoutClosing(Path file, Path content) throws IOException {
            File target = file.toFile();
            target.createNewFile();
            RandomAccessFile randomAccessFile = new RandomAccessFile(target, "rw");
            randomAccessFile.seek((index - 1)*partitionSize);
            int totalBytes = ((contentLength < 0)?(partitionSize):(contentLength));
            byte[] bytes = new byte[totalBytes];
            FileInputStream reader = new FileInputStream(content.toFile());
            reader.read(bytes);
            reader.close();
            randomAccessFile.write(bytes);
            randomAccessFile.seek((index - 1)*partitionSize + totalBytes);
            return randomAccessFile;
        }
    }

    private class FirstPartition extends Partition {
        FirstPartition(int partitionSize, Path content) {
            super(1, partitionSize-2, content, -1);
        }

        @Override
        void burn(Path file) throws IOException {
            RandomAccessFile randomAccessFile = super.burnWithoutClosing(file, content);
            randomAccessFile.writeByte(0x55);
            randomAccessFile.writeByte(0xAA);
            randomAccessFile.close();
        }
    }
}
