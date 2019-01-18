package definitions.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Method {
    MethodType method() default MethodType.GET;
}
