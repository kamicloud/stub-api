package com.kamicloud.stub.core.segments.java;

import com.kamicloud.stub.core.interfaces.CombinerInterface;

public class ClassAttributeCombiner implements CombinerInterface {
    private ClassCombiner classCombiner;
    private String type;
    private String name;
    private String access;


    public ClassAttributeCombiner(ClassCombiner classCombiner, String name, String access) {
        this.classCombiner = classCombiner;
        this.access = access == null ? "public" : access;
        this.name = name;

        classCombiner.addAttribute(this);
    }

    public String toString() {
        return "    " + access + " " + type + " " + name + ";\n";
    }

    public ClassCombiner getClassCombiner() {
        return classCombiner;
    }
}
