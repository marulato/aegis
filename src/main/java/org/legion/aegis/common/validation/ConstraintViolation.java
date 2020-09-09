package org.legion.aegis.common.validation;

public class ConstraintViolation {

    private String validatedFieldName;
    private Object validatedFieldValue;
    private String message;
    private String[] profile;
    private Class<?> validationType;

    public ConstraintViolation(String validatedFieldName, Object validatedFieldValue,
                               String message, String[] profile, Class<?> validationType) {
        this.validatedFieldName = validatedFieldName;
        this.validatedFieldValue = validatedFieldValue;
        this.message = message;
        this.profile = profile;
        this.validationType = validationType;
    }

    public String getValidatedFieldName() {
        return validatedFieldName;
    }

    public void setValidatedFieldName(String validatedFieldName) {
        this.validatedFieldName = validatedFieldName;
    }

    public Object getValidatedFieldValue() {
        return validatedFieldValue;
    }

    public void setValidatedFieldValue(Object validatedFieldValue) {
        this.validatedFieldValue = validatedFieldValue;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String[] getProfile() {
        return profile;
    }

    public void setProfile(String[] profile) {
        this.profile = profile;
    }

    public Class<?> getValidationType() {
        return validationType;
    }

    public void setValidationType(Class<?> validationType) {
        this.validationType = validationType;
    }
}
