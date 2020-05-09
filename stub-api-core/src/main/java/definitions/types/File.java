package definitions.types;

import definitions.official.TypeSpec;

public class File extends Type {
    @Override
    public TypeSpec getSpec() {
        return TypeSpec.FILE;
    }

    @Override
    public String getComment() {
        return "file";
    }

    @Override
    public String getLaravelRule() {
        return "file";
    }

    @Override
    public String getLaravelParam() {
        return "file";
    }
}
