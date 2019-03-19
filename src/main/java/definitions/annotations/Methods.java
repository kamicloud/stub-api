package definitions.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Methods {
    String name = "Methods";
    MethodType[] value() default MethodType.GET;
}
