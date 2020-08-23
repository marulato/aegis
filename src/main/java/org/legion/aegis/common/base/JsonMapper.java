package org.legion.aegis.common.base;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class JsonMapper {

    private Map<String, Object> map;

    public JsonMapper() {
        map = new HashMap<>();
    }

    public void addJson(String key, Object value) {
        if (key != null && value != null) {
            map.put(key, value);
        }
    }

    public String toJsonString() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(map);
    }

    public Map<String, Object> toJson() {
        return map;
    }
}
