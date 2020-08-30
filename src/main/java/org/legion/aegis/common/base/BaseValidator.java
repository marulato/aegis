package org.legion.aegis.common.base;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public interface BaseValidator {

    default Map<String, String> doValidate(HttpServletRequest request) {
        return new HashMap<>();
    }

    default Map<String, String> doValidate(Map<String, String> params) {
        return new HashMap<>();
    }

}
