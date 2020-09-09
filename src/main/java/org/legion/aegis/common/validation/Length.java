package org.legion.aegis.common.validation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Length {

    int min() default 0;

    int max() default Integer.MAX_VALUE;

    String message();

    String[] profile() default {};
}
