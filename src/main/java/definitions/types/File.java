package definitions.types;

public class File implements CustomizeInterface {
    @Override
    public String getType() {
        return "file";
    }

    @Override
    public String getRule() {
        return "file";
    }

    @Override
    public TypeSpec getSpec() {
        return TypeSpec.FILE;
    }

    @Override
    public String getParam() {
        return "file";
    }

    @Override
    public String getComment() {
        return "file";
    }
}
