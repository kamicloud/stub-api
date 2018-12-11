package com.kamicloud.generator;

import com.kamicloud.generator.stubs.OutputStub;
import com.kamicloud.generator.writers.AutoTestWriter;

public class AutoTest {
    public static void main(String[] args) {
        Generator generator = new Generator();
        OutputStub output = generator.parse();


        output.addObserver(new AutoTestWriter());

        output.notifyObservers();
    }
}
