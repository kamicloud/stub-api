package com.kamicloud.stub.core.generators.components.common;

import com.kamicloud.stub.core.config.DefaultProfileUtil;
import com.kamicloud.stub.core.interfaces.CombinerInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class FileCombiner extends Combiner implements FileWriter, CombinerInterface {
    protected String fileName;

    public void setFileName(String fileName) {
        this.fileName = fileName;

        String suffix = DefaultProfileUtil.getConfig().getGenerators().getForceSuffix();

        if (suffix != null) {
            this.fileName = fileName + suffix;
        }
    }

    public String getOutputFilename() {

        return fileName;
    }

    public boolean exists() {
        return new File(getOutputFilename()).exists();
    }

    public void toFile() throws IOException {
        String fileName = getOutputFilename();
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

        outputStreamWriter.write(toString());

        outputStreamWriter.close();
        fileOutputStream.close();
    }

    public static void build(String fileName, String content) throws IOException {
        build(fileName, content, false);
    }

    public static void build(String fileName, String content, boolean replace) throws IOException {
        if (new File(fileName).exists() && !replace) {
            return;
        }
        FileCombiner fileCombiner = new FileCombiner();
        fileCombiner.setFileName(fileName);
        fileCombiner.addBlock(new MultiLinesCombiner(content));

        fileCombiner.toFile();
    }
}
