package definitions.annotations;

public @interface Parameter {
    String field() default "";
    boolean optional() default false;
}
