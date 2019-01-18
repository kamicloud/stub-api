package definitions.annotations;

public enum MethodType {
    GET, POST, PUT, PATCH, DELETE, UPDATE;

    public static MethodType transform(String name) {
        switch (name) {
            case "GET":
                return MethodType.GET;
            case "POST":
                return MethodType.POST;
            case "PUT":
                return MethodType.PUT;
            case "PATCH":
                return MethodType.PATCH;
            case "DELETE":
                return MethodType.DELETE;
            case "UPDATE":
                return MethodType.UPDATE;
            default:
                return null;
        }
    }
}
