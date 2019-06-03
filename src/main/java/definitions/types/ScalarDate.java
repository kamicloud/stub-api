package definitions.types;

import definitions.official.TypeSpec;

public class ScalarDate extends Type {
    @Override
    public TypeSpec getSpec() {
        return TypeSpec.DATE;
    }

    @Override
    public String getComment() {
        return null;
    }

    @Override
    public String getLaravelRule() {
        return "date_format:Y-m-d H:i:s";
    }

    @Override
    public String getLaravelParam() {
        return "Y-m-d H:i:s";
    }
}
