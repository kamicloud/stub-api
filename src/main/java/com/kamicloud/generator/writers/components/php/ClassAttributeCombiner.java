package com.kamicloud.generator.writers.components.php;

import com.kamicloud.generator.interfaces.CombinerInterface;

public class ClassAttributeCombiner implements CombinerInterface {
    private String name;
    private String access;


    public ClassAttributeCombiner(String name, String access) {
        this.access = access == null ? "public" : access;
        this.name = name;
    }

    @Override
    public String write() {
        return "    " + access + " $" + name + ";\n";
    }
}
