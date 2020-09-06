package org.legion.aegis.admin.validator;

import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.entity.UserRoleAssign;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.base.BaseValidator;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;

import java.util.*;

public class UserModifyValidator implements BaseValidator {

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, String> doValidate(Map<String, Object> params) {
        Map<String, String> errors = new HashMap<>();
        String name = (String) params.get("name");
        String email = (String) params.get("email");
        String deactivateAt = (String) params.get("deactivateAt");
        String[] newAddProjects = ((List<String>)params.get("projectSelector")).toArray(new String[0]);
        String userId = (String) params.get("userId");
        boolean isSupervisor = false;
        if (StringUtils.isBlank(name) || name.length() < 3 || name.length() > 32) {
            errors.put("name", "名字长度为3~32字符");
        }
        if (!ValidationUtils.isValidEmail(email)) {
            errors.put("email", "请输入正确的邮件地址");
        }
        UserAccountService userAccountService = SpringUtils.getBean(UserAccountService.class);
        UserAccount userAccount = userAccountService.getUserById(StringUtils.parseIfIsLong(userId));

        UserRoleAssign roleAssign = userAccountService.getRoleAssigments(userAccount.getId()).get(0);
        isSupervisor = UserAccountService.isSupervisor(roleAssign.getRoleId());

        Date deactivateDate = DateUtils.parseDatetime(deactivateAt);
        if (deactivateDate == null || deactivateDate.before(new Date())) {
            errors.put("deactivateAt", "失效日期不能早于当前日期");
        }
        ProjectService projectService = SpringUtils.getBean(ProjectService.class);
        if (!isSupervisor) {
            for (String newProjectId : newAddProjects) {
                if (StringUtils.parseIfIsLong(newProjectId) == null
                        || projectService.getProjectById(StringUtils.parseIfIsLong(newProjectId), false) == null) {
                    errors.put("projectSelector", "请选择正确的项目");
                    break;
                }
            }
        } else {
            for (String newProjectId : newAddProjects) {
                if (StringUtils.parseIfIsLong(newProjectId) == null
                        || projectService.getProjectGroupById(StringUtils.parseIfIsLong(newProjectId)) == null) {
                    errors.put("projectSelector", "请选择正确的项目");
                    break;
                }
            }
        }

        return errors;
    }
}
