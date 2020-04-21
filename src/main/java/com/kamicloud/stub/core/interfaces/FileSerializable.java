package com.kamicloud.stub.core.interfaces;

import java.io.IOException;

public interface FileSerializable {

    void setFileName(String fileName);

    void toFile() throws IOException;
}
