package org.legion.aegis.common.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> List<T> subList(List<T> list, int start, int end) {
        List<T> ext = new ArrayList<>();
        if (list != null && start >= 0 && end <= list.size() && end >= start) {
            for (int i = start; i < end; i++) {
                ext.add(list.get(i));
            }
        }
        return ext;
    }

    public static <T extends Serializable> List<T> getModifiableList(List<T> list, boolean isDeepClone) {
        List<T> ext = new ArrayList<>();
        if (list != null) {
            for (T t : list) {
                if (!isDeepClone) {
                    ext.add(t);
                } else {
                    ext.add(BeanUtils.deepClone(t));
                }
            }
        }
        return ext;
    }

}
