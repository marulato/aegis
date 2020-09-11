package org.legion.aegis.common.utils;

import org.legion.aegis.admin.entity.MasterCode;
import org.legion.aegis.common.cache.CachePool;
import org.legion.aegis.common.cache.MasterCodeCache;
import org.legion.aegis.general.dao.MasterCodeDAO;
import java.util.ArrayList;
import java.util.List;

public class MasterCodeUtils {

    private static final MasterCodeCache cache = CachePool.getCache(MasterCodeCache.KEY, MasterCodeCache.class);
    private static final MasterCodeDAO masterCodeDAO = SpringUtils.getBean(MasterCodeDAO.class);

    public static MasterCode getMasterCode(String type, String value) {
        MasterCode masterCode = cache.get(type, value);
        if (masterCode == null) {
            masterCodeDAO.getMasterCode(type, value);
        }
        return masterCode;
    }

    public static List<MasterCode> getMasterCodeByType(String type) {
        if (StringUtils.isNotBlank(type)) {
            return cache.getAllByType(type);
        }
        return new ArrayList<>();
    }

    public static String[] getCodeArrayByType(String type) {
        List<MasterCode> masterCodes = getMasterCodeByType(type);
        String[] array = new String[masterCodes.size()];
        for (int i = 0; i < masterCodes.size(); i++) {
            array[i] = masterCodes.get(i).getCode();
        }
        return array;
    }


}
