package definitions.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 设计上用来表示枚举类型是字符串
 *
 * 在服务端生成数据时，强制使用字符串类型
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface StringEnum {
}
