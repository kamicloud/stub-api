package com.kamicloud.generator.writers.components.common;

import com.kamicloud.generator.interfaces.CombinerInterface;

import java.util.LinkedList;

public class Combiner implements CombinerInterface {
    protected LinkedList<CombinerInterface> blocks = new LinkedList<>();

    public void addBlock(CombinerInterface block) {
        blocks.add(block);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        blocks.forEach(block -> stringBuilder.append(block));

        return stringBuilder.toString();
    }
}
