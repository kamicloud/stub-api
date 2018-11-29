package com.zhh.generator.annotations;

public @interface Parameter {
    String field() default "";
    boolean optional() default false;
}
