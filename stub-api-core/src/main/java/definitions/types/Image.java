package definitions.types;

import definitions.official.TypeSpec;

public class Image extends Type {
    @Override
    public String getLaravelRule() {
        return "image";
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.FILE;
    }

    @Override
    public String getLaravelParam() {
        return "image";
    }

    @Override
    public String getComment() {
        return "image";
    }
}
