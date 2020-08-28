package org.legion.aegis.common.base;

import org.legion.aegis.common.utils.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class BaseDto implements Serializable, Cloneable {

    private String createdAt;
    private String createdBy;
    private String updatedAt;
    private String updatedBy;

    public <T> void mapParameters(HttpServletRequest request, T dto) throws Exception {
        if (dto != null) {
            Class<?> type = dto.getClass();
            Field[] allFields = type.getDeclaredFields();
            for (Field field : allFields) {
                BeanUtils.setValue(field, type, dto, request.getParameter(field.getName()));
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
}
