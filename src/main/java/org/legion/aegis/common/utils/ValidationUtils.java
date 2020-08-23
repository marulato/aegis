package org.legion.aegis.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationUtils {

    public static final String SINGLE_EMAIL_REGEX = "(?:(?:[A-Za-z0-9\\-_@!#$%&'*+/=?^`{|}~]|" +
            "(?:\\\\[\\x00-\\xFF]?)|(?:\"[\\x00-\\xFF]*\"))+(?:\\.(?:(?:[A-Za-z0-9\\-_@!#$%&'*+" +
            "/=?^`{|}~])|(?:\\\\[\\x00-\\xFF]?)|(?:\"[\\x00-\\xFF]*\"))+)*)@(?:(?:[A-Za-z0-9]" +
            "(?:[A-Za-z0-9-]*[A-Za-z0-9])?\\.)+(?:(?:[A-Za-z0-9]*[A-Za-z][A-Za-z0-9]*)" +
            "(?:[A-Za-z0-9-]*[A-Za-z0-9])?))";

    public static boolean isValidEmail(String emailAddress) {
        Pattern pattern = Pattern.compile(SINGLE_EMAIL_REGEX);
        Matcher matcher = pattern.matcher(emailAddress == null ? "" : emailAddress);
        return matcher.matches();
    }
}
