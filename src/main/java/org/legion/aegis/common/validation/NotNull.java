package org.legion.aegis.common.validation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface NotNull {

    String message();

    String[] profile() default {};
}
