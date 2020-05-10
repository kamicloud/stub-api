package definitions.types;

import definitions.official.SpecInterface;
import definitions.official.TypeSpec;

public class Mobile extends Type {
    @Override
    public String getLaravelRule() {
        return "string";
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.STRING;
    }

    @Override
    public String getLaravelParam() {
        return "string";
    }

    @Override
    public String getComment() {
        return "手机号";
    }
}
