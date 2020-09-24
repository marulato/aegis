package org.legion.aegis.common.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.legion.aegis.admin.entity.MasterCode;
import org.legion.aegis.admin.entity.UserRole;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class MasterCodeCache implements ICache<String, MasterCode> {

    private static final Cache<String, Map<String, MasterCode>> cache = CacheBuilder.newBuilder().build();
    private static final Cache<String, UserRole> roleCache = CacheBuilder.newBuilder().build();
    public static final String KEY = "org.legion.aegis.common.cache.MasterCodeCache";
    private static final Logger log = LoggerFactory.getLogger(MasterCodeCache.class);

    static {
        MasterCodeCache codeCache = new MasterCodeCache();
        CachePool.setCache(KEY, codeCache);
    }


    @Override
    public MasterCode get(String typeAndCode) {
        if (StringUtils.isNotBlank(typeAndCode)) {
            String[] typeAndCodeArr = typeAndCode.split(":");
            if (typeAndCodeArr.length == 2) {
                return get(typeAndCodeArr[0], typeAndCodeArr[1]);
            }
        }
        return null;
    }

    public MasterCode get(String type, String code) {
        Map<String, MasterCode> map = cache.getIfPresent(type == null ? "" : type);
        if (map != null && StringUtils.isNotBlank(code)) {
            MasterCode masterCode = map.get(code);
            if (masterCode != null) {
                try {
                    return (MasterCode) masterCode.clone();
                } catch (CloneNotSupportedException e) {
                    log.error("", e);
                }
            }
        }
        return null;
    }

    public List<MasterCode> getAllByType(String type) {
        Map<String, MasterCode> map = cache.getIfPresent(type == null ? "" : type);
        List<MasterCode> list = new ArrayList<>();
        if (map != null) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                if (map.get(key) != null) {
                    MasterCode masterCode = BeanUtils.deepClone(map.get(key));
                    list.add(masterCode);
                }
            }
            list.sort(Comparator.comparing(MasterCode::getDisplayOrder));
        }
        return List.copyOf(list);
    }


    public UserRole getUserRole(String roleId) {
        return roleCache.getIfPresent(roleId);
    }

    @Override
    public void set(String key, MasterCode value) {

    }

    public void set(MasterCode masterCode) {
        if (masterCode != null) {
            Map<String, MasterCode> map = cache.getIfPresent(masterCode.getType());
            if (map == null) {
                map = new HashMap<>();
                map.put(masterCode.getCode(), masterCode);
                cache.put(masterCode.getType(), map);
            } else {
                map.put(masterCode.getCode(), masterCode);
            }
        }
    }


}
