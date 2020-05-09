package definitions.types;

import definitions.official.TypeSpec;

public class ScalarBoolean extends Type {
    @Override
    public String getLaravelRule() {
        return "boolean";
    }

    @Override
    public String getLaravelParam() {
        return null;
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.BOOLEAN;
    }

    @Override
    public String getComment() {
        return null;
    }
}
