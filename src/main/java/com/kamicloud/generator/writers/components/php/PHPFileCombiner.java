package com.kamicloud.generator.writers.components.php;

import com.kamicloud.generator.interfaces.CombinerInterface;
import com.kamicloud.generator.writers.components.common.FileCombiner;
import com.kamicloud.generator.writers.components.common.Combiner;
import com.kamicloud.generator.writers.components.common.FileWriter;

import java.io.IOException;

public class PHPFileCombiner extends Combiner implements FileWriter, CombinerInterface {
    protected String fileName;

    @Override
    public String toString() {
        return "<?php\n\n" + super.toString();
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void toFile() throws IOException {
        FileCombiner fileCombiner = new FileCombiner();

        fileCombiner.setFileName(fileName);
        fileCombiner.addBlock(this);

        fileCombiner.toFile();
    }
}
