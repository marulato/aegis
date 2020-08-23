package org.legion.aegis.common.utils;


import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.base.PropertyMapping;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

public class BeanUtils {

    public static <T> T mapFromDto(BaseDto dto, Class<T> type) throws Exception {
        if (dto != null && type != null) {
            T instance = type.getConstructor().newInstance();
            Class<?> dtoType = dto.getClass();
            Field[] dtoFields = dtoType.getDeclaredFields();
            for (Field dtoField : dtoFields) {
                Field targetField;
                String fieldName = dtoField.getName();
                if (dtoField.isAnnotationPresent(PropertyMapping.class)) {
                    PropertyMapping mapping = dtoField.getAnnotation(PropertyMapping.class);
                    fieldName = mapping.value();
                }
                try {
                    targetField = type.getDeclaredField(fieldName);
                } catch (Exception e) {
                    continue;
                }
                if (targetField.getType() == dtoField.getType()) {
                    setValue(targetField, type, instance, getValue(dtoField, dtoType, dto));
                }
                if (targetField.getType() == Date.class || targetField.getType() == java.sql.Date.class) {
                    String dateString = (String) getValue(dtoField, dtoType, dto);
                    if (StringUtils.isNotBlank(dateString)) {
                        setValue(targetField, type, instance, DateUtils.parseDate(dateString));
                    }
                }
                if (targetField.getType() == int.class || targetField.getType() == Integer.class) {
                    String intString = (String) getValue(dtoField, dtoType, dto);
                    if (StringUtils.isNotBlank(intString)) {
                        setValue(targetField, type, instance, Integer.parseInt(intString));
                    }
                }
                if (targetField.getType() == long.class || targetField.getType() == Long.class) {
                    String longString = (String) getValue(dtoField, dtoType, dto);
                    if (StringUtils.isNotBlank(longString)) {
                        setValue(targetField, type, instance, Long.parseLong(longString));
                    }
                }
                if (targetField.getType() == double.class || targetField.getType() == Double.class) {
                    String doubleString = (String) getValue(dtoField, dtoType, dto);
                    if (StringUtils.isNotBlank(doubleString)) {
                        setValue(targetField, type, instance, Double.parseDouble(doubleString));
                    }
                }
            }
            return instance;
        }
        return null;
    }


    public static Object getValue(Field field, Class<?> objClass, Object instance) throws Exception {
        String getter = "get";
        Object value;
        field.setAccessible(true);
        if (field.getType() == boolean.class) {
            getter = "is";
        }
        getter += StringUtils.capitalCharacter(field.getName(), 0);
        Method getterMethod = objClass.getDeclaredMethod(getter);
        int modifier = getterMethod.getModifiers();
        if (Modifier.isPublic(modifier) && !Modifier.isAbstract(modifier)
                && !Modifier.isStatic(modifier) && getterMethod.getReturnType() == field.getType()) {
            getterMethod.setAccessible(true);
            value = getterMethod.invoke(instance);
        } else {
            value = field.get(instance);
        }
        return value;
    }

    public static void setValue(Field field, Class<?> objClass, Object instance, Object value) throws Exception {
        String setter = "set";
        field.setAccessible(true);
        setter += StringUtils.capitalCharacter(field.getName(), 0);
        Method setterMethod = objClass.getDeclaredMethod(setter, field.getType());
        int modifier = setterMethod.getModifiers();
        if (Modifier.isPublic(modifier) && !Modifier.isAbstract(modifier)
                && !Modifier.isStatic(modifier) && setterMethod.getReturnType() == field.getType()) {
            setterMethod.setAccessible(true);
            setterMethod.invoke(instance, value);
        } else {
            field.set(instance, value);
        }
    }
}
