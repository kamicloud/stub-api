package definitions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Laravel 验证数据时的规则
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface LaravelValidateRule {
    String[] value() default "";
}
