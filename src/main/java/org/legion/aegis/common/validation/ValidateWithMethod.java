package org.legion.aegis.common.validation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidateWithMethod {

    Class<?> type() default void.class;

    String methodName();

    String[] parameters() default {};

    String message();

    String[] profile() default {};

    @Target(ElementType.FIELD)
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @interface List {
        ValidateWithMethod[] value();
    }
}
