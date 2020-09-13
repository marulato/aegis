package org.legion.aegis.common.validation;

import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.Reflections;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;

public class CommonValidator {

    public static List<ConstraintViolation> validate(Object obj) throws Exception {
        return validate(obj, null);
    }

    public static List<ConstraintViolation> validate(Object obj, String profile) throws Exception {
        List<ConstraintViolation> violations = new ArrayList<>();
        if (obj != null) {
            Class<?> objClass = obj.getClass();
            Field[] allFields = objClass.getDeclaredFields();
            for (Field field : allFields) {
                field.setAccessible(true);
                ConstraintField constraintField = new ConstraintField(field, profile, obj);
               checkAnnotation_NotNull(constraintField, violations);
               checkAnnotation_NotEmpty(constraintField, violations);
               checkAnnotation_NotBlank(constraintField, violations);
               checkAnnotation_MatchesPattern(constraintField, violations);
               checkAnnotation_MemberOf(constraintField, violations);
               checkAnnotation_NotMemberOf(constraintField, violations);
               checkAnnotation_Length(constraintField, violations);
               checkAnnotation_Email(constraintField, violations);
               checkAnnotation_Date(constraintField, violations);
               checkAnnotation_ValidateWithMethod(constraintField, violations);
            }
        }
        return violations;
    }

    private static boolean isProfileMatch(String profile, String[] annoProfile) {

        if (StringUtils.isEmpty(profile) && (annoProfile == null || annoProfile.length == 0)) {
            return true;
        }
        if (StringUtils.isEmpty(profile) && annoProfile != null && annoProfile.length > 0) {
            return true;
        }
        if (StringUtils.isNotEmpty(profile) && (annoProfile == null || annoProfile.length == 0)) {
            return false;
        }
        if (StringUtils.isNotEmpty(profile) && annoProfile != null && annoProfile.length > 0) {
            List<String> list = Arrays.asList(annoProfile);
            return list.contains(profile);
        }
        return false;
    }

    private static void checkAnnotation_ValidateWithMethod(ConstraintField constraintField,
                                                           final List<ConstraintViolation> violations) throws Exception {
        Field field = constraintField.getField();
        if (constraintField.isAnnotationPresent(ValidateWithMethod.List.class)) {
            ValidateWithMethod.List list = field.getAnnotation(ValidateWithMethod.List.class);
            ValidateWithMethod[] vars = list.value();
            for (ValidateWithMethod var : vars) {
                validateWithMethod(field, var, constraintField.getInputProfile(), constraintField.getInstanceBelongsTo(), violations);
            }
        } else if (constraintField.isAnnotationPresent(ValidateWithMethod.class)) {
            ValidateWithMethod validateWithMethod = field.getAnnotation(ValidateWithMethod.class);
            validateWithMethod(constraintField.getField(), validateWithMethod, constraintField.getInputProfile(),
                    constraintField.getInstanceBelongsTo(), violations);
        }
    }

    private static void validateWithMethod(Field field, ValidateWithMethod validateWithMethod,
                                           String profile, final Object instance, final List<ConstraintViolation> violations) throws Exception {
        if (isProfileMatch(profile, validateWithMethod.profile())) {
            Object value = Reflections.getValue(field, instance.getClass(), instance);
            String methodName = validateWithMethod.methodName();
            String[] parameters = validateWithMethod.parameters();
            Class<?> methodInType = validateWithMethod.type();
            Object methodInstance = instance;
            List<Object> argsList = new ArrayList<>();
            Class<?>[] argTypesArray = new Class[parameters.length + 1];
            Arrays.fill(argTypesArray, String.class);
            argTypesArray[0] = field.getType();
            if (methodInType != void.class) {
                methodInstance = methodInType.getConstructor().newInstance();
            }
            Method method = methodInstance.getClass().getDeclaredMethod(methodName, argTypesArray);
            if (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class) {
                method.setAccessible(true);
                argsList.add(value);
                argsList.addAll(Arrays.asList(parameters));
                boolean result = (boolean) method.invoke(methodInstance, argsList.toArray());
                if (!result) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            validateWithMethod.message(), validateWithMethod.profile(), ValidateWithMethod.class));
                }
            } else {
                throw new NoSuchMethodException("Could not find a method that meets the requirements described by '@ValidateWithMethod'");
            }
        }
    }

    private static void checkAnnotation_NotNull(final ConstraintField constraintField,
                                                final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(NotNull.class)) {
            Field field = constraintField.getField();
            NotNull notNull = field.getAnnotation(NotNull.class);
            if (isProfileMatch(constraintField.getInputProfile(), notNull.profile())) {
                if (Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo()) == null) {
                    violations.add(new ConstraintViolation(field.getName(), null,
                            notNull.message(), notNull.profile(), NotNull.class));
                }
            }
        }
    }

    private static void checkAnnotation_NotEmpty(final ConstraintField constraintField,
                                                 final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(NotEmpty.class)) {
            Field field = constraintField.getField();
            NotEmpty notEmpty = field.getAnnotation(NotEmpty.class);
            if (isProfileMatch(constraintField.getInputProfile(), notEmpty.profile())) {
                Object value = Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo());
                if (value == null || StringUtils.isEmpty(String.valueOf(value))) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            notEmpty.message(), notEmpty.profile(), NotNull.class));
                }
            }
        }
    }

    private static void checkAnnotation_NotBlank(final ConstraintField constraintField,
                                                 final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(NotBlank.class)) {
            Field field = constraintField.getField();
            NotBlank notBlank = field.getAnnotation(NotBlank.class);
            if (isProfileMatch(constraintField.getInputProfile(), notBlank.profile())) {
                Object value = Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo());
                if (value == null || StringUtils.isBlank(String.valueOf(value))) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            notBlank.message(), notBlank.profile(), NotBlank.class));
                }
            }
        }
    }

    private static void checkAnnotation_MemberOf(final ConstraintField constraintField,
                                                 final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(MemberOf.class)) {
            Field field = constraintField.getField();
            MemberOf memberOf = field.getAnnotation(MemberOf.class);
            if (isProfileMatch(constraintField.getInputProfile(), memberOf.profile())) {
                List<String> members = List.of(memberOf.value());
                Object value = Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo());
                if (!(value instanceof String) || !members.contains(String.valueOf(value))) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            memberOf.message(), memberOf.profile(), MemberOf.class));
                }
            }
        }
    }

    private static void checkAnnotation_NotMemberOf(final ConstraintField constraintField,
                                                 final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(NotMemberOf.class)) {
            Field field = constraintField.getField();
            NotMemberOf notMemberOf = field.getAnnotation(NotMemberOf.class);
            if (isProfileMatch(constraintField.getInputProfile(), notMemberOf.profile())) {
                List<String> members = List.of(notMemberOf.value());
                Object value = Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo());
                if (!(value instanceof String) || members.contains(String.valueOf(value))) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            notMemberOf.message(), notMemberOf.profile(), NotMemberOf.class));
                }
            }
        }
    }

    private static void checkAnnotation_MatchesPattern(final ConstraintField constraintField,
                                                 final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(MatchesPattern.class)) {
            Field field = constraintField.getField();
            MatchesPattern matchesPattern = field.getAnnotation(MatchesPattern.class);
            if (isProfileMatch(constraintField.getInputProfile(), matchesPattern.profile())) {
                Pattern pattern = Pattern.compile(matchesPattern.pattern());
                Object value = Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo());
                if (!(value instanceof String) || !pattern.matcher(String.valueOf(value)).matches()) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            matchesPattern.message(), matchesPattern.profile(), MatchesPattern.class));
                }
            }
        }
    }

    private static void checkAnnotation_Length(final ConstraintField constraintField,
                                                       final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(Length.class)) {
            Field field = constraintField.getField();
            Length length = field.getAnnotation(Length.class);
            if (isProfileMatch(constraintField.getInputProfile(), length.profile())) {
                Object value = Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo());
                if (value == null && length.min() == 0) {
                    return;
                }
                if (!(value instanceof String) || ((String) value).length() < length.min()
                        || ((String) value).length() > length.max()) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            length.message(), length.profile(), Length.class));
                }
            }
        }
    }

    private static void checkAnnotation_Email(final ConstraintField constraintField,
                                                       final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(Email.class)) {
            Field field = constraintField.getField();
            Email email = field.getAnnotation(Email.class);
            if (isProfileMatch(constraintField.getInputProfile(), email.profile())) {
                Object value = Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo());
                if (!(value instanceof String) || !ValidationUtils.isValidEmail((String) value)) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            email.message(), email.profile(), Email.class));
                }
            }
        }
    }

    private static void checkAnnotation_Date(final ConstraintField constraintField,
                                             final List<ConstraintViolation> violations) throws Exception {
        if (constraintField.isAnnotationPresent(Date.class)) {
            Field field = constraintField.getField();
            Date date = field.getAnnotation(Date.class);
            if (isProfileMatch(constraintField.getInputProfile(), date.profile())) {
                Object value = Reflections.getValue(field, constraintField.getInstanceType(), constraintField.getInstanceBelongsTo());
                if (!(value instanceof String) || DateUtils.parseDatetime((String) value, date.pattern()) == null) {
                    violations.add(new ConstraintViolation(field.getName(), value,
                            date.message(), date.profile(), Date.class));
                }
            }
        }
    }

}
