package com.kamicloud.generator.writers.components.php;

import com.kamicloud.generator.stubs.EnumStub;
import com.kamicloud.generator.writers.CombinerInterface;

import java.util.ArrayList;

public class ClassConstantCombiner implements CombinerInterface {
    private String name;
    private ArrayList<String> lines = new ArrayList<>();
    private EnumStub.EnumStubItemType type;

    public ClassConstantCombiner(String name, ArrayList<String> lines) {

    }

    public ClassConstantCombiner(String name, String line, EnumStub.EnumStubItemType type) {
        this.name = name;
        this.lines.add(line);
        this.type = type;
    }

    @Override
    public String write() {
        String intend = "    ";
        String valueString = String.join("\n" + intend, lines);
        String prefix = intend + "const " + name + " = ";
        if (type != null) {
            if (type == EnumStub.EnumStubItemType.INTEGER) {
                return prefix + valueString + ";\n";
            } else {
                return prefix + "'" + valueString + "';\n";
            }
        }

        return prefix + valueString + ";\n";
    }
}
