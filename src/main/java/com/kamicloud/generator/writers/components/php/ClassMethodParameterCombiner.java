package com.kamicloud.generator.writers.components.php;

import com.kamicloud.generator.writers.CombinerInterface;

public class ClassMethodParameterCombiner implements CombinerInterface {

    private String type;
    private String name;
    private String defaultValue;

    public ClassMethodParameterCombiner(String name) {
        this.name = name;
    }

    public ClassMethodParameterCombiner(String name, String type) {
        this.type = type;
        this.name = name;
    }

    public ClassMethodParameterCombiner(String name, String type, String defaultValue) {
        this.type = type;
        this.name = name;
        this.defaultValue = defaultValue;
    }

    @Override
    public String write() {
        String string;
        if (type == null) {
            string = "$" + name;
        } else {
            string = type + " $" + name;
        }
        if (defaultValue != null) {
            string += " = " + defaultValue;
        }

        return string;
    }
}
