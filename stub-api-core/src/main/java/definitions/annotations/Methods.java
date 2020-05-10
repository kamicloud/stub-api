package definitions.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Methods {
    MethodType[] value() default MethodType.GET;
}
