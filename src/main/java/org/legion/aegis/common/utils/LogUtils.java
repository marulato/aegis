package org.legion.aegis.common.utils;

public class LogUtils {

    public static String separator(String separator, int length) {
        if (StringUtils.isNotEmpty(separator) && length > 0) {
            return separator.repeat(length);
        }
        return separator;
    }

    public static String equal(int length) {
        return separator("=", length);
    }

    public static String asterisk(int length) {
        return separator("*", length);
    }

    public static String minus(int length) {
        return separator("-", length);
    }

    public static String around(String log, String sep, int length) {
        return print(separator(sep, length) + log + separator(sep, length));
    }

    public static String around(String log, String sep) {
        return print(separator(sep, 20) + log + separator(sep, 20));
    }

    public static String around(String log) {
        return print(separator("=", 20) + log + separator("=", 20));
    }

    public static String wrapVariable(Object obj) {
        if (obj != null) {
            return " [" + obj + "] ";
        }
        return null;
    }

    public static String print(String... logs) {
        if (logs != null && logs.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String log : logs) {
                builder.append(log);
            }
            return builder.toString().replace('\n', ' ').
                    replace('\r', ' ').
                    replace('\t', ' ');
        }
        return null;
    }
}
