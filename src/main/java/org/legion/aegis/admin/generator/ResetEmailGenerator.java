package org.legion.aegis.admin.generator;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.docgen.EmailTemplateGenerator;
import java.util.HashMap;
import java.util.Map;

public class ResetEmailGenerator extends EmailTemplateGenerator {
    private final String code;

    public ResetEmailGenerator(String code) {
        this.code = code;
    }

    @Override
    public String getSubject() {
        return "您正在修改您的邮箱地址";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        AppContext context = AppContext.getFromWebThread();
        params.put("loginId", context.getLoginId());
        params.put("code", code);
        return params;
    }

    @Override
    public String getTemplate() {
        return "resetEmailVerification.ftl";
    }
}
