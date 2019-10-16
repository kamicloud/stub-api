package definitions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 表示该数据结构不作版本划分，不会生成version代码，只有BO
 *
 * 这个存在时可以不声明AsBO
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Versionless {
}
