package com.kamicloud.generator;

import com.kamicloud.generator.annotations.API;
import com.kamicloud.generator.annotations.MethodType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;

public class Debug {
    public static void main(String[] args) {
        Template template = new Template();
        try {
            Class<?> c = template.getClass();

            Class<?>[] b = c.getDeclaredClasses();
            Class<?>[] controllers = b[0].getDeclaredClasses();
            Class<?>[] userActions = controllers[1].getDeclaredClasses();

            Field[] fields = userActions[1].getDeclaredFields();
            Annotation[] annotations = userActions[1].getDeclaredAnnotations();
//            Annotation[] annotations = userActions[1].getAnn();


            Annotation an = userActions[1].getAnnotation(API.class);
            MethodType[] mt = ((API) an).methods();

            Proxy annotation = (Proxy) annotations[0];
            String x = "x";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
