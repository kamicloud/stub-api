package com.kamicloud.stub.core.segments.php;

import com.kamicloud.stub.core.interfaces.CombinerInterface;
import com.kamicloud.stub.core.segments.common.FileCombiner;
import com.kamicloud.stub.core.segments.common.Combiner;
import com.kamicloud.stub.core.segments.common.FileWriter;

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
