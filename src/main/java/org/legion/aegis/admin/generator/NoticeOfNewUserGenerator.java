package org.legion.aegis.admin.generator;

import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.entity.UserRole;
import org.legion.aegis.admin.entity.UserRoleAssign;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.docgen.PdfTemplateGenerator;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.SpringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoticeOfNewUserGenerator extends PdfTemplateGenerator {

    private final UserAccount account;

    public NoticeOfNewUserGenerator(UserAccount account) {
        super();
        this.account = account;
    }
    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        if (account != null) {
            params.put("loginId", account.getLoginId());
            params.put("password", account.getOriginalPwd());
            params.put("email", account.getEmail());
            UserAccountService accountService = SpringUtils.getBean(UserAccountService.class);
            List<UserRoleAssign> assigns = accountService.getRoleAssigments(account.getId());
            UserRole role = accountService.getRoleById(assigns.get(0).getRoleId());
            params.put("role", role.getRoleName());
            params.put("dateGenerated", DateUtils.getDateString(new Date(), "yyyy/MM/dd"));
        }
        return params;
    }

    @Override
    public String getTemplate() {
        return "newUserNotice.ftl";
    }
}
