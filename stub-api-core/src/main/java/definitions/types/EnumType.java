package definitions.types;

import definitions.types.Type;
import definitions.official.TypeSpec;

public class EnumType extends Type {
    @Override
    public TypeSpec getSpec() {
        return TypeSpec.ENUM;
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
