package definitions.types;


import definitions.official.TypeSpec;

public class Json extends Type {
    @Override
    public String getLaravelParam() {
        return null;
    }

    @Override
    public String getLaravelRule() {
        return null;
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.JSON;
    }

    @Override
    public String getComment() {
        return null;
    }
}
