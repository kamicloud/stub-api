package com.zhh.generator.writers;

import java.io.File;
import java.util.Observer;

abstract class BaseWriter implements Observer {
    File dir = new File("");
    BaseWriter() {

    }
}
