package com.kamicloud.generator.writers.components.java;

import com.kamicloud.generator.interfaces.CombinerInterface;

public class ClassMethodParameterCombiner implements CombinerInterface, AddImportInterface {

    private ClassMethodCombiner classMethodCombiner;
    private String type;
    private String name;
    private String defaultValue;

    public ClassMethodParameterCombiner(ClassMethodCombiner classMethodCombiner, String name) {
        this(classMethodCombiner, name, null, null);
    }

    public ClassMethodParameterCombiner(ClassMethodCombiner classMethodCombiner, String name, String type) {
        this(classMethodCombiner, name, type, null);
    }

    public ClassMethodParameterCombiner(ClassMethodCombiner classMethodCombiner, String name, String type, String defaultValue) {
        this.classMethodCombiner = classMethodCombiner;
        if (type != null) {
            this.type = addImport(type);
        }
        this.name = name;
        this.defaultValue = defaultValue;

        classMethodCombiner.addParameter(this);
    }

    @Override
    public String toString() {
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

    @Override
    public String addImport(String use) {
        return this.classMethodCombiner.addImport(use);
    }
}
