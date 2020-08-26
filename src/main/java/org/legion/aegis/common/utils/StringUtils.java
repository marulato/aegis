package org.legion.aegis.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static boolean isEmpty(String src) {
        return src == null || src.length() == 0;
    }

    public static boolean isNotEmpty(String src) {
        return !isEmpty(src);
    }

    public static boolean isBlank(String src) {
        return src == null || src.trim().length() == 0;
    }

    public static boolean isNotBlank(String src) {
        return !isBlank(src);
    }

    public static String insert(String src, String value, int point) {
        if (isNotEmpty(src) && isNotEmpty(value)) {
            StringBuilder builder = new StringBuilder(src);
            builder.insert(point, value);
            return builder.toString();
        }
        return src;
    }
    public static String[] convertToArray(String value) {
        if (value.isBlank())
            return new String[0];
        String array[] = new String[value.length()];
        for (int i = 0; i < value.length(); i++) {
            array[i] = String.valueOf(value.charAt(i));
        }
        return array;
    }

    public static boolean isLetter(String src) {
        Pattern pattern = Pattern.compile("[a-zA-Z]");
        Matcher matcher = pattern.matcher(src);
        return matcher.matches();
    }

    public static int getLetterIndex(String letter) {
        if (isLetter(letter) && letter.length() == 1)
            return ALPHABET.indexOf(letter.toUpperCase()) + 1;
        return 0;
    }

    public static String capitalCharacter(String src, int index) {
        if (isNotEmpty(src)) {
            char ch = src.charAt(index);
            if(ch >= 'a' && ch <= 'z' || ch >= 'A' && ch <= 'Z') {
                ch = Character.toUpperCase(ch);
                StringBuilder sBuilder = new StringBuilder();
                sBuilder.append(src.substring(0, index)).append(ch).append(src.substring(index + 1));
                return sBuilder.toString();
            }
        }
        return src;
    }

    public static Boolean parseBoolean(String s) {
        return "true".equalsIgnoreCase(s) || "Y".equalsIgnoreCase(s) || "YES".equalsIgnoreCase(s);
    }


    public static boolean hasUnderline(String property) {
        return property.contains("_");
    }

    public static String toTitleCase(String property) {
        char[] chars = property.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }

    public static boolean hasTitleCase(String property) {
        char[] chars = property.toCharArray();
        for (char ch : chars) {
            if (Character.isUpperCase(ch)) return true;
        }
        return false;
    }

    public static String [] splitByUpperCase(String property) {
        char[] chars = property.toCharArray();
        StringBuilder fragment = new StringBuilder();
        for (char ch : chars) {
            if (ch >= 'A' && ch <= 'Z') {
                fragment.append("_");
            }
            fragment.append(ch);
        }
        return fragment.toString().split("_");
    }

    public static boolean isInteger(String integer) {
        try {
            Integer.parseInt(integer);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public static Integer parseIfIsInteger(String src) {
        try {
            return Integer.parseInt(src);
        } catch (Exception e) {
            return null;
        }
    }

    public static Long parseIfIsLong(String src) {
        try {
            return Long.parseLong(src);
        } catch (Exception e) {
            return null;
        }
    }
}
