package com.kamicloud.stub.core.utils;

import java.io.File;

public class FileUtil {
    public static void deleteAllFilesOfDir(File path) {
        if (!path.exists())
            return;
        if (path.isFile()) {
            path.delete();
            return;
        }
        File[] files = path.listFiles();
        assert files != null;
        for (File file : files) {
            deleteAllFilesOfDir(file);
        }
        path.delete();
    }

    public static void deleteAllFilesOfDir(String path) {
        deleteAllFilesOfDir(new File(path));
    }
}
