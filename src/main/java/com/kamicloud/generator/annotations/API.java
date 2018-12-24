package com.kamicloud.generator.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface API {
    String name = "API";
    String methods = "methods";

    MethodType[] methods() default { MethodType.GET };
    String path() default "";
    boolean transactional() default false;
//    Middleware[] middlewares() default {};
//    boolean forceHttps() default false;
}
