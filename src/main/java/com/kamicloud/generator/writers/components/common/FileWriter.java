package com.kamicloud.generator.writers.components.common;

import java.io.IOException;

public interface FileWriter {

    void setFileName(String fileName);

    void toFile() throws IOException;
}
