package org.legion.aegis.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ArrayUtils {

    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

    public static boolean isEmpty(byte[] array) {
        return array == null || array.length == 0;
    }

    public static byte[] extract(byte[] array, int start, int end) {
        if (!isEmpty(array) && start <= end && end < array.length) {
            byte[] copy = new byte[end - start + 1];
            System.arraycopy(array, start, copy, 0, (end -start + 1));
            return copy;
        }
        return new byte[0];
    }

    public static String[] extract(String[] array, int start, int end) {
        if (!isEmpty(array) && start <= end && end < array.length) {
            String[] copy = new String[end - start + 1];
            System.arraycopy(array, start, copy, 0, (end -start + 1));
            return copy;
        }
        return new String[0];
    }

    public static String[] removeEmpty(String[] array) {
        if (!isEmpty(array)) {
            List<String> list = new ArrayList<>(Arrays.asList(array));
            list.removeIf(StringUtils::isEmpty);
            String[] result = new String[list.size()];
            list.toArray(result);
            return result;
        }
        return array;
    }

    public static char[] joint(char[] head, char[] tail) {
        if (head == null)
            return tail;
        if (tail == null)
            return head;
        char[] link = new char[head.length + tail.length];
        System.arraycopy(head, 0, link, 0, head.length);
        System.arraycopy(tail, 0, link, head.length, tail.length);
        return link;
    }

    public static String[] joint(String[] head, String[] tail) {
        if (head == null)
            return tail;
        if (tail == null)
            return head;
        String[] link = new String[head.length + tail.length];
        System.arraycopy(head, 0, link, 0, head.length);
        System.arraycopy(tail, 0, link, head.length, tail.length);
        return link;
    }

    public static char[] joint(char[]... elements) {
        if (elements == null)
            return new char[0];
        char[] temp = new char[0];
        for (char[] element : elements) {
            temp = joint(temp, element);
        }
        return temp;
    }

    public static String[] joint(String[]... elements) {
        if (elements == null)
            return new String[0];
        String[] temp = new String[0];
        for (String[] element : elements) {
            temp = joint(temp, element);
        }
        return temp;
    }

    public static String toString(String[] array, String connector) {
        if (array != null && array.length > 0) {
            StringBuilder builder = new StringBuilder();
            for (String s : array) {
                builder.append(s).append(connector);
            }
            builder.deleteCharAt(builder.lastIndexOf(connector));
            return builder.toString();
        }
        return null;
    }
}
