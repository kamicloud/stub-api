package com.zhh.generator.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Optional {
    String name = "Optional";
}
