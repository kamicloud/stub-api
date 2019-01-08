package com.kamicloud.generator;

@SuppressWarnings("unused")
public class TemplateList {
    public static Class<?>[] templates = new Class[] {
            TemplateV1.class,
            TemplateV1_1.class,
    };

    public static Class<?> errorsTemplate = Errors.class;
    
    public static Class<?> currentTemplate = TemplateV1.class;
}
