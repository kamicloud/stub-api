package definitions.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface DBField {
    /**
     * 变量原名，使用驼峰命名，网络传输转换成下划线
     */
    String value() default "";
}
