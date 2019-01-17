package definitions;

public @interface Parameter {
    String field() default "";
    boolean optional() default false;
}
