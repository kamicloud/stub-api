package com.kamicloud.stub.core.generators.components.php;

import com.kamicloud.stub.core.interfaces.CombinerInterface;

public class ClassMethodParameterCombiner implements CombinerInterface, AddUseInterface {

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
            this.type = addUse(type);
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
    public String addUse(String use) {
        return this.classMethodCombiner.addUse(use);
    }
}
