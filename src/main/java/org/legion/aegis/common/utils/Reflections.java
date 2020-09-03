package org.legion.aegis.common.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Reflections {

    public static Object getValue(Field field, Class<?> objClass, Object instance) throws Exception {
        String getter = "get";
        Object value;
        field.setAccessible(true);
        if (field.getType() == boolean.class || field.getType() == Boolean.class) {
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
