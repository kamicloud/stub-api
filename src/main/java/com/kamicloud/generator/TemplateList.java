package com.kamicloud.generator;

public class TemplateList {
    public static Class<?>[] templates = new Class[] {
            Template.class,
            TemplateV1_1.class,
    };
    
    public static Class<?> currentTemplate = Template.class;
}
