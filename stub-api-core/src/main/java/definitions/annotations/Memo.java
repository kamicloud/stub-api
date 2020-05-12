package definitions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface Memo {
    /**
     * 注释内容，会被传递到代码和文档中
     * @return String[]
     */
    String[] value() default "";

    /**
     * 别名，预留的映射
     * @return String[]
     */
    String[] aliases() default "";

    /**
     * 不渲染的端类型，默认各端均渲染
     * @return Endpoint[]
     */
    Endpoint[] ignores() default Endpoint.NONE;
}
