package templates;

import java.util.HashMap;

@SuppressWarnings("unused")
public class TemplateList {
    public static Class<?>[] templates = new Class[] {
        TemplateV1.class,
        // TemplateV1_1.class,
    };

    public static Class<? extends Enum<?>> errorsTemplate = Errors.class;

    public static Class<?> currentTemplate = TemplateV1.class;

    /**
     * 自动测试时 _role 对应的数据表
     */
    public HashMap<String, String> testcaseRoleMap = new HashMap<String, String>() {{
        put("User", "App\\Models\\User");
        put("AdminUser", "App\\Models\\AdminUser");
    }};
}
