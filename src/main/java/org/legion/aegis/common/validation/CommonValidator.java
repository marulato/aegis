package org.legion.aegis.common.validation;


import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.StringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonValidator {

    public static Map<String, List<String>> doValidation(Object obj, String profile) throws Exception {
        Map<String, List<String>> validationMap = new HashMap<>();
        if (obj != null) {
            Class<?> objClass = obj.getClass();
            Field[] allFileds = objClass.getDeclaredFields();
            for (Field field : allFileds) {
                field.setAccessible(true);
                validationMap.put(field.getName(), new ArrayList<>());
                if (field.isAnnotationPresent(NotNull.class)) {
                    NotNull anno = field.getAnnotation(NotNull.class);
                    if (BeanUtils.getValue(field, objClass, obj) == null && isProfileMatch(profile, anno.profile())) {
                        validationMap.get(field.getName()).add(field.getAnnotation(NotNull.class).message());
                    }
                }

                if (field.isAnnotationPresent(NotEmpty.class)) {
                    Object value = BeanUtils.getValue(field, objClass, obj);
                    NotEmpty anno = field.getAnnotation(NotEmpty.class);
                    if (isProfileMatch(profile, anno.profile())) {
                        if (value == null || value instanceof String && ((String) value).isEmpty()) {
                            validationMap.get(field.getName()).add(field.getAnnotation(NotEmpty.class).message());
                        }
                    }
                }

                if (field.isAnnotationPresent(ValidateWithRegex.class)) {
                    Object value = BeanUtils.getValue(field, objClass, obj);
                    ValidateWithRegex anno = field.getAnnotation(ValidateWithRegex.class);
                    if (isProfileMatch(profile, anno.profile())) {
                        if (!(field.getType() == String.class)) {
                            throw new ClassCastException("Can NOT convert the type of " + field.getName() + ": "
                                    + field.getType() + " to java.lang.String");
                        }
                        Pattern pattern = Pattern.compile(anno.regex());
                        if (value != null) {
                            Matcher matcher = pattern.matcher((String) value);
                            if (!matcher.matches()) {
                                validationMap.get(field.getName()).add(anno.message());
                            }
                        } else {
                            validationMap.get(field.getName()).add(anno.message());
                        }
                    }
                }

                if (field.isAnnotationPresent(IsIn.class)) {
                    Object value = BeanUtils.getValue(field, objClass, obj);
                    IsIn isIn = field.getAnnotation(IsIn.class);
                    if (isProfileMatch(profile, isIn.profile())) {
                        if (!(field.getType() == String.class)) {
                            throw new ClassCastException("Can NOT convert the type of " + field.getName() + ": "
                                    + field.getType() + " to java.lang.String");
                        }
                        String[] array = isIn.value();
                        List<String> list = Arrays.asList(array);
                        if (!list.contains((String) value)) {
                            validationMap.get(field.getName()).add(isIn.message());
                        }
                    }
                }

                if (field.isAnnotationPresent(ValidateWithMethod.class)) {
                    ValidateWithMethod anno = field.getAnnotation(ValidateWithMethod.class);
                    Object value = BeanUtils.getValue(field, objClass, obj);
                    checkValidateWithMethod(field, anno, value, obj, objClass, validationMap, profile);
                }

                if (field.isAnnotationPresent(ValidateWithMethodList.class)) {
                    Object value = BeanUtils.getValue(field, objClass, obj);
                    ValidateWithMethodList anno = field.getAnnotation(ValidateWithMethodList.class);
                    ValidateWithMethod[] validateWithMethods = anno.methodList();
                    for (ValidateWithMethod validateWithMethod : validateWithMethods) {
                        checkValidateWithMethod(field, validateWithMethod, value, obj, objClass, validationMap, profile);
                    }
                }
                if (validationMap.get(field.getName()).isEmpty()) {
                    validationMap.remove(field.getName());
                }
            }
        }
        return validationMap;
    }

    private static boolean isProfileMatch(String profile, String annoProfile) {
        if (StringUtils.isEmpty(profile) && StringUtils.isEmpty(annoProfile))
            return true;
        if (StringUtils.isNotEmpty(profile) && StringUtils.isNotEmpty(annoProfile))
            return profile.equals(annoProfile);
        return !StringUtils.isEmpty(profile) || !StringUtils.isNotEmpty(annoProfile);
    }


    private static void checkValidateWithMethod(Field field, ValidateWithMethod anno, Object value, Object obj,
                                                Class<?> objClass, Map<String, List<String>> validationMap, String profile) throws Exception {
        if (isProfileMatch(profile, anno.profile())) {
            String methodName = anno.method();
            String[] parameters = anno.parameters();
            Object[] argsArray = new String[parameters.length + 1];
            Class<?>[] argTypesArray = new Class[parameters.length + 1];
            Arrays.fill(argTypesArray, String.class);
            Method method = objClass.getDeclaredMethod(methodName, argTypesArray);
            if (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class) {
                method.setAccessible(true);
                argsArray[0] = String.valueOf(value);
                System.arraycopy(parameters, 0, argsArray, 1, parameters.length);
                boolean result = (boolean) method.invoke(obj, argsArray);
                if (!result) {
                    validationMap.get(field.getName()).add(anno.message());
                }
            } else {
                throw new NoSuchMethodException("Could not find a method that meets the requirements described by '@ValidateWithMethod'");
            }
        }
    }

}
