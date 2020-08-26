package org.legion.aegis.admin.generator;

import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.common.docgen.EmailTemplateGenerator;
import java.util.HashMap;
import java.util.Map;

public class NewUserEmailGenerator extends EmailTemplateGenerator {

    private final UserAccount account;

    public NewUserEmailGenerator(UserAccount account) {
        super();
        this.account =account;
    }
    @Override
    public String getSubject() {
        return "已为您创建AEGIS账户";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> param = new HashMap<>();
        param.put("loginId", account.getLoginId());
        param.put("password", account.getOriginalPwd());
        return param;
    }

    @Override
    public String getTemplate() {
        return "newUserEmail.ftl";
    }
}
