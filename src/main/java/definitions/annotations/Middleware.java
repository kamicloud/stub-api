package definitions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Middleware {
    String name = "Middleware";
    String[] value() default "";
}
