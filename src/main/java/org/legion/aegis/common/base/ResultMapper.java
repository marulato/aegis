package org.legion.aegis.common.base;

import org.legion.aegis.common.utils.StringUtils;
import java.util.HashMap;
import java.util.Map;

public class ResultMapper {

    private Map<String, Object> map;

    public ResultMapper() {
        map = new HashMap<>();
    }

    public void add(String key, Object obj) {
        if (StringUtils.isNotBlank(key)) {
            map.put(key, obj);
        }
    }

    public void remove(String key) {
        if (StringUtils.isNotBlank(key)) {
            map.remove(key);
        }
    }

    public Object get(String key) {
        return map.get(key);
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public int getSize() {
        return map.size();
    }
}
