package definitions.types;

import definitions.official.TypeSpec;

public class DateYm extends Type {
    @Override
    public TypeSpec getSpec() {
        return TypeSpec.DATE;
    }

    @Override
    public String getComment() {
        return "Y-m 例如 2019-01";
    }

    public String getLaravelRule() {
        return "date_format:Y-m";
    }

    @Override
    public String getLaravelParam() {
        return "Y-m";
    }
}
