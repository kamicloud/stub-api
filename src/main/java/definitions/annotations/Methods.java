package definitions.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Methods {
    String name = "methods";
    MethodType[] value() default MethodType.GET;
}
