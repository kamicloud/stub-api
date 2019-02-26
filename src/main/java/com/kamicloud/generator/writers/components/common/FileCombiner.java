package com.kamicloud.generator.writers.components.common;

import com.kamicloud.generator.interfaces.CombinerInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

public class FileCombiner extends Combiner implements FileWriter, CombinerInterface {
    protected String fileName;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void toFile() throws IOException {
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
}
