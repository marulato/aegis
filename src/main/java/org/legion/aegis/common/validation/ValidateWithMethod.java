package org.legion.aegis.common.validation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateWithMethod {


    String method();

    String[] parameters() default {};

    String message();

    String profile() default "";
}
