package org.legion.aegis.admin.validator;

import org.legion.aegis.common.base.BaseValidator;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordValidator implements BaseValidator {

    @Override
    public Map<String, String> doValidate(Map<String, Object> params) {
        Map<String, String> errors = new HashMap<>();
        String pwd1 = (String) params.get("pwd1");
        String pwd2 = (String) params.get("pwd2");
        if (StringUtils.isBlank(pwd1)) {
            errors.put("pwd1", "密码不能为空");
        }
        if (StringUtils.isNotBlank(pwd1) && !pwd1.equals(pwd2)) {
            errors.put("pwd2", "输入的密码不一致");
        }
        if (!ValidationUtils.isValidPassword(pwd1)) {
            errors.put("pwd1", "密码必须为6~20位长度的任意字符");
        }
        return errors;
    }
}
