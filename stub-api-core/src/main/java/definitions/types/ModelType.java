package definitions.types;

import definitions.official.TypeSpec;

public class ModelType extends Type {
    @Override
    public TypeSpec getSpec() {
        return TypeSpec.MODEL;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getLaravelRule() {
        return null;
    }

    @Override
    public String getLaravelParam() {
        return null;
    }
}
