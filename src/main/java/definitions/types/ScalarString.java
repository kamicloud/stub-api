package definitions.types;

import definitions.official.TypeSpec;

public class ScalarString extends Type {
    @Override
    public String getLaravelRule() {
        return "string";
    }

    @Override
    public String getLaravelParam() {
        return null;
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.STRING;
    }

    @Override
    public String getComment() {
        return "字符串";
    }
}
