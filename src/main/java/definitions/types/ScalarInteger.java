package definitions.types;

import definitions.official.TypeSpec;

public class ScalarInteger extends Type {
    @Override
    public String getLaravelParam() {
        return null;
    }

    @Override
    public String getLaravelRule() {
        return "integer";
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.INTEGER;
    }

    @Override
    public String getComment() {
        return null;
    }
}
