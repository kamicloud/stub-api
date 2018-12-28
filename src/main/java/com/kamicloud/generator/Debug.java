package com.kamicloud.generator;

import com.kamicloud.generator.annotations.API;
import com.kamicloud.generator.annotations.MethodType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;

public class Debug {
    public static void main(String[] args) {
        Template template = new Template();
        try {
            Class<?> templateClass = template.getClass();

            Arrays.asList(templateClass.getDeclaredClasses()).forEach(element -> {
                String name = element.getName();
                String canonicalName = element.getCanonicalName();
                String simpleName = element.getSimpleName();
                if (simpleName.equals("Controllers")) {
                    Arrays.asList(element.getDeclaredClasses()).forEach(controllerTemplate -> {
                        Arrays.asList(controllerTemplate.getDeclaredClasses()).forEach(actionTemplate -> {
                            Arrays.asList(actionTemplate.getDeclaredFields()).forEach(parameterStub -> {

                                Class<?> type = parameterStub.getType();
                                Boolean is = type.isMemberClass();
                                Type types = parameterStub.getGenericType();
                                String fullTypeName = type.getTypeName();
                                String typeName = type.getSimpleName();
                                String nn = type.getName();
                                String xx = "";

                            });

                        });
                    });
                }
            });
//            Class<?>[] controllers = b[0].getDeclaredClasses();
//            Class<?>[] userActions = controllers[1].getDeclaredClasses();
//
//            Field[] fields = userActions[1].getDeclaredFields();
//            Annotation[] annotations = userActions[1].getDeclaredAnnotations();
//            Annotation[] annotations = userActions[1].getAnn();
//
//
//            Annotation an = userActions[1].getAnnotation(API.class);
//            MethodType[] mt = ((API) an).methods();
//
//            Proxy annotation = (Proxy) annotations[0];
//            String x = "x";
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
