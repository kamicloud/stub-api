package definitions.types;

public class Image implements CustomizeInterface {
    @Override
    public String getType() {
        return "image";
    }

    @Override
    public String getRule() {
        return "image";
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.FILE;
    }

    @Override
    public String getParam() {
        return "image";
    }

    @Override
    public String getComment() {
        return "image";
    }
}
