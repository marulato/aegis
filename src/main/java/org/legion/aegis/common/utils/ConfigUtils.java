package org.legion.aegis.common.utils;


import org.legion.aegis.common.cache.CachePool;
import org.legion.aegis.common.cache.ConfigCache;

public class ConfigUtils {

    private static ConfigCache configCache = CachePool.getCache(ConfigCache.KEY, ConfigCache.class);

    public static String get(String key) {
        return configCache.get(key);
    }
}
