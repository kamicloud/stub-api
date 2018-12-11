package com.kamicloud.generator.annotations;

public @interface API {
    MethodType[] methods() default { MethodType.GET };
    String path() default "";
    boolean transactional() default false;
//    Middleware[] middlewares() default {};
//    boolean forceHttps() default false;
}
