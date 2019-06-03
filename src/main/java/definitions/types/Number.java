package definitions.types;

public class Number implements CustomizeInterface {
    @Override
    public String getType() {
        return "numeric";
    }

    @Override
    public String getRule() {
        return "numeric";
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.NUMBER;
    }

    @Override
    public String getParam() {
        return "numeric";
    }

    @Override
    public String getComment() {
        return "数字";
    }
}
