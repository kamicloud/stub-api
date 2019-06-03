package definitions.types;

import definitions.official.TypeSpec;

public class ScalarFloat extends Type {
    @Override
    public TypeSpec getSpec() {
        return TypeSpec.FLOAT;
    }

    @Override
    public String getComment() {
        return "数字";
    }

    @Override
    public String getLaravelRule() {
        return "numeric";
    }

    @Override
    public String getLaravelParam() {
        return "numeric";
    }
}
