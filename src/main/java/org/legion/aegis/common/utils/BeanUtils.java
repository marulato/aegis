package org.legion.aegis.common.utils;


import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.base.PropertyMapping;
import org.legion.aegis.common.jpa.annotation.NotColumn;

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
                        setValue(targetField, type, instance, DateUtils.parseDatetime(dateString));
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

    public static <T> T mapFromPO(BasePO po, Class<T> type, String dateFormat) throws Exception {
        if (StringUtils.isEmpty(dateFormat)) {
            dateFormat = "yyyy/MM/dd HH:mm:ss";
        }
        if (po != null && type != null) {
            T instance = type.getConstructor().newInstance();
            Class<?> poType = po.getClass();
            Class<?> basePoClass = poType.getSuperclass();
            Class<?> baseDtoClass = type.getSuperclass();
            Field[] fields = poType.getDeclaredFields();
            for (Field field : fields) {
                Field targetField;
                try {
                    targetField = type.getDeclaredField(field.getName());
                } catch (Exception e) {
                    continue;
                }
                if (field.isAnnotationPresent(NotColumn.class)) {
                    continue;
                }
                if (field.getType() == Date.class) {
                    Date date = (Date) getValue(field, poType, po);
                    setValue(targetField, type, instance, DateUtils.getDateString(date, dateFormat));
                } else {
                    setValue(targetField, type, instance,
                            StringUtils.getNonEmpty(String.valueOf(getValue(field, poType, po))));
                }
            }
            Field[] auditFields = basePoClass.getDeclaredFields();
            for (Field auditField : auditFields) {
                Field dtoAuditField = baseDtoClass.getDeclaredField(auditField.getName());
                if (dtoAuditField.getType() == String.class && auditField.getType() == Date.class) {
                    Date date = (Date) getValue(auditField, basePoClass, po);
                    setValue(dtoAuditField, baseDtoClass, instance, DateUtils.getDateString(date, dateFormat));
                } else {
                    setValue(dtoAuditField, baseDtoClass, instance,
                            StringUtils.getNonEmpty(String.valueOf(getValue(auditField, basePoClass, po))));
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
                && !Modifier.isStatic(modifier)) {
            setterMethod.setAccessible(true);
            setterMethod.invoke(instance, value);
        } else {
            field.set(instance, value);
        }
    }
}
