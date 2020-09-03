package org.legion.aegis.common.validation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

public class ConstraintField {

    private Field field;
    private Class<?> instanceType;
    private String inputProfile;
    private Object instanceBelongsTo;

    public ConstraintField(Field field, String profile, Object instanceBelongsTo) {
        this.field = field;
        this.inputProfile = profile;
        this.instanceBelongsTo = instanceBelongsTo;
        this.instanceType = instanceBelongsTo.getClass();
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annotationType) {
        return field.isAnnotationPresent(annotationType);
    }


    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public String getInputProfile() {
        return inputProfile;
    }

    public void setInputProfile(String inputProfile) {
        this.inputProfile = inputProfile;
    }

    public Object getInstanceBelongsTo() {
        return instanceBelongsTo;
    }

    public void setInstanceBelongsTo(Object instanceBelongsTo) {
        this.instanceBelongsTo = instanceBelongsTo;
    }

    public Class<?> getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(Class<?> instanceType) {
        this.instanceType = instanceType;
    }
}
