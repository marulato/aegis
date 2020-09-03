package org.legion.aegis.common.base;

import org.legion.aegis.common.utils.Reflections;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;

public abstract class BaseDto implements Serializable, Cloneable {

    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;
    private String role;
    private String userId;

    public <T> void mapParameters(HttpServletRequest request, T dto) throws Exception {
        if (dto != null) {
            Class<?> type = dto.getClass();
            Field[] allFields = type.getDeclaredFields();
            for (Field field : allFields) {
                Reflections.setValue(field, type, dto, request.getParameter(field.getName()));
            }
        }
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
