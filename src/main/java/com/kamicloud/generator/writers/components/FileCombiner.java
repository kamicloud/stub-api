package com.kamicloud.generator.writers.components;

import com.kamicloud.generator.interfaces.CombinerInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;

public class FileCombiner implements CombinerInterface {
    protected String fileName;
    protected LinkedList<CombinerInterface> blocks = new LinkedList<>();

    @Override
    public String write() {
        return String.join("", blocks);
    }

    public void toFile() throws IOException {
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(fileName);
        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8);

        outputStreamWriter.write(write());

        outputStreamWriter.close();
        fileOutputStream.close();
    }
}
