package org.legion.aegis.common.base;

import org.legion.aegis.common.utils.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public abstract class BaseDto implements Serializable, Cloneable {

    public <T> void mapParameters(HttpServletRequest request, T dto) throws Exception {
        if (dto != null) {
            Class<?> type = dto.getClass();
            Field[] allFields = type.getDeclaredFields();
            for (Field field : allFields) {
                BeanUtils.setValue(field, type, dto, request.getParameter(field.getName()));
            }
        }
    }
}
