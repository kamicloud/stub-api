package definitions.types;

public class Mobile implements CustomizeInterface {
    @Override
    public String getType() {
        return "string";
    }

    @Override
    public String getRule() {
        return "string";
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.STRING;
    }

    @Override
    public String getParam() {
        return "string";
    }

    @Override
    public String getComment() {
        return "手机号";
    }
}
