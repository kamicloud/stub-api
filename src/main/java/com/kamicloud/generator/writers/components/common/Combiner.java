package com.kamicloud.generator.writers.components.common;

import com.kamicloud.generator.interfaces.CombinerInterface;

import java.util.LinkedList;

public class Combiner implements CombinerInterface {
    protected LinkedList<CombinerInterface> blocks = new LinkedList<>();

    public void addBlock(CombinerInterface block) {
        blocks.add(block);
    }

    public void addLine(String line) {
        if (line == null) {
            line = "";
        }
        blocks.add(new MultiLinesCombiner(line));
    }

    public void addLine() {
        addLine("");
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        blocks.forEach(block -> stringBuilder.append(block));

        return stringBuilder.toString();
    }
}
