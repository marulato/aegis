package org.legion.aegis.common.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.ORMEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;

public class ORMCache implements ICache <Class<? extends BasePO>, ORMEntity> {

    private static final Logger log = LoggerFactory.getLogger(ORMCache.class);
    public static final String KEY = "org.legion.aegis.common.cache.ORMCache";

    static  {
        ORMCache ormCache = new ORMCache();
        ormCache.init();
        CachePool.setCache(KEY, ormCache);
    }

    private LoadingCache<Class<? extends BasePO>, ORMEntity> loadingCache;

    private void init() {
        loadingCache = CacheBuilder.newBuilder().build(new CacheLoader<>() {
            @Override
            public ORMEntity load(Class<? extends BasePO> aClass) throws Exception {
                log.info("new instance cached [" + aClass.getName() + "]");
                return ORMEntity.getInstance(aClass);
            }
        });
    }

    @Override
    public ORMEntity get(Class<? extends BasePO> key) {
        ORMEntity ormEntity = null;
        try {
            ormEntity = loadingCache.get(key);
        } catch (ExecutionException e) {
            log.error("", e);
        }
        return ormEntity;
    }

    @Override
    public void set(Class<? extends BasePO> key, ORMEntity value) {

    }
}
