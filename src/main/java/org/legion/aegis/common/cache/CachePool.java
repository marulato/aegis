package org.legion.aegis.common.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CachePool {

    private static final Map<String, ICache<?, ?>> cachePool = new HashMap<>();
    private static final List<String> classList = new ArrayList<>();

    public static <T> T getCache(String key, Class<T> type) {
        if (!classList.contains(key)) {
            try {
                Class.forName(key);
                classList.add(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return (T) cachePool.get(key);
    }

    public static ICache getCache(String key) {
        if (!classList.contains(key)) {
            try {
                Class.forName(key);
                classList.add(key);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return cachePool.get(key);
    }

    public static void setCache(String key, ICache<?, ?> cache) {
        cachePool.put(key, cache);
    }

}
