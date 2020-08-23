package org.legion.aegis.common.jpa.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface PrimaryKey {

    boolean autoIncrement();
}
