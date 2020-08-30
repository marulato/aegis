package org.legion.aegis.admin.validator;

import org.legion.aegis.common.base.BaseValidator;
import org.legion.aegis.common.utils.StringUtils;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ModuleValidator implements BaseValidator {


    @Override
    public Map<String, String> doValidate(Map<String, String> params) {
        Map<String, String> errors = new HashMap<>();
        String moduleName = params.get("moduleName");
        String moduleDesc = params.get("moduleDesc");
        if (StringUtils.isBlank(moduleName) || moduleName.length() > 64) {
            errors.put("moduleName", "请输入正确的名称，不超过64字符");
        }
        if (StringUtils.isNotBlank(moduleDesc) && moduleDesc.length() > 500) {
            errors.put("moduleDesc", "请输入正确的描述，不超过500字符");
        }
        return errors;
    }
}
