package org.legion.aegis.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static final String SINGLE_EMAIL_REGEX = "(?:(?:[A-Za-z0-9\\-_@!#$%&'*+/=?^`{|}~]|" +
            "(?:\\\\[\\x00-\\xFF]?)|(?:\"[\\x00-\\xFF]*\"))+(?:\\.(?:(?:[A-Za-z0-9\\-_@!#$%&'*+" +
            "/=?^`{|}~])|(?:\\\\[\\x00-\\xFF]?)|(?:\"[\\x00-\\xFF]*\"))+)*)@(?:(?:[A-Za-z0-9]" +
            "(?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+(?:(?:[A-Za-z0-9]*[A-Za-z][A-Za-z0-9]*)" +
            "(?:[A-Za-z0-9-]*[A-Za-z0-9])?))";

    public static final String LOGIN_ID_REGEX = "[A-Za-z0-9_]{4,16}";

    public static final String PASSWORD_REGEX = ".{6,20}";

    public static boolean isValidEmail(String emailAddress) {
        return matcher(SINGLE_EMAIL_REGEX, emailAddress).matches();
    }

    public static boolean isValidLoginId(String loginId) {
        return matcher(LOGIN_ID_REGEX, loginId).matches();
    }

    public static boolean isValidPassword(String pwd) {
        return matcher(PASSWORD_REGEX, pwd).matches();
    }

    private static Matcher matcher(String regex, String src) {
        Pattern pattern = Pattern.compile(regex);
        return pattern.matcher(src == null ? "" : src);
    }
}
