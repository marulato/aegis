package org.legion.aegis.common.base;

import org.legion.aegis.admin.entity.MasterCode;
import org.legion.aegis.common.cache.CachePool;
import org.legion.aegis.common.cache.ICache;
import org.legion.aegis.common.cache.MasterCodeCache;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class AjaxResponseManager {

    private final AjaxResponseBody ajaxResponseBody;
    private final Map<String, String> errorCodes;
    private final List<Object> dataObjects;
    private static final Logger log = LoggerFactory.getLogger(AjaxResponseManager.class);

    private AjaxResponseManager(int code){
        ajaxResponseBody = new AjaxResponseBody();
        ajaxResponseBody.setStatus(code);
        errorCodes = new HashMap<>();
        dataObjects = new ArrayList<>();
    }

    public static AjaxResponseManager create(int responseCode) {
        log.info("Ajax Response STATUS -> " + responseCode);
        return new AjaxResponseManager(responseCode);
    }

    public void addError(String field, String errorCode) {
        errorCodes.put(StringUtils.isBlank(field) ? "default" : field, errorCode);
    }
    public void addErrors(Map<String, String> errorCode) {
        if (errorCode != null) {
            errorCodes.putAll(errorCode);
        }
    }

    public void addValidations(List<ConstraintViolation> violations) {
        for (ConstraintViolation violation : violations) {
            if (StringUtils.isNotBlank(errorCodes.get(violation.getValidatedFieldName()))) {
                continue;
            }
            errorCodes.put(violation.getValidatedFieldName(), violation.getMessage());
        }
        log.info("Validation NOT Pass -> " + violations);
    }

    public void addDataObject(Object object) {
        if (object != null && errorCodes.isEmpty()) {
            dataObjects.add(object);
        }
    }

    public void addDataObjects(List<?> objects) {
        if (objects != null && errorCodes.isEmpty()) {
            dataObjects.addAll(objects);
        }
    }

    public AjaxResponseBody respond() {
        ajaxResponseBody.setRespondAt(new Date());
        if (!errorCodes.isEmpty()) {
            ICache<String, MasterCode> masterCodeCache = CachePool.getCache(MasterCodeCache.KEY, MasterCodeCache.class);
            Map<String, String> data = new HashMap<>();
            Set<String> keySet = errorCodes.keySet();
            for (String field : keySet) {
                MasterCode masterCode = masterCodeCache.get("cm.error:" + errorCodes.get(field));
                if (masterCode != null) {
                    data.put(field, masterCode.getDescription());
                } else {
                    data.put(field, errorCodes.get(field));
                }
            }
            ajaxResponseBody.setData(data);
        } else {
            ajaxResponseBody.setData(dataObjects);
        }
        return ajaxResponseBody;
    }


}
