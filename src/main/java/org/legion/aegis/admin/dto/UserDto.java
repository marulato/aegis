package org.legion.aegis.admin.dto;

import org.legion.aegis.admin.dao.ProjectDAO;
import org.legion.aegis.admin.dao.UserAccountDAO;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;
import org.legion.aegis.common.validation.MatchesPattern;
import org.legion.aegis.common.validation.ValidateWithMethod;

import java.util.Date;

public class UserDto extends BaseDto {

    @MatchesPattern(pattern = ValidationUtils.LOGIN_ID_REGEX, message = "用户名只能包含数字，字母和下划线，长度4~16位")
    private String loginId;
    @MatchesPattern(pattern = ValidationUtils.SINGLE_EMAIL_REGEX, message = "请检查邮箱地址的格式")
    private String email;
    @ValidateWithMethod(methodName = "validateProject", message = "请添加正确的项目")
    private String[] project;
    @ValidateWithMethod(methodName = "validateRole", message = "请选择正确的权限")
    private String role;
    @ValidateWithMethod(methodName = "validateRange", message = "请选择正确的日期范围")
    private String effectiveRange;
    private String password;

    private Date activatedAt;
    private Date deactivatedAt;

    private boolean validateProject(String[] project) {
        if (project != null) {
            ProjectDAO projectDAO = SpringUtils.getBean(ProjectDAO.class);
            for (String id : project) {
                return projectDAO.getProjectById(StringUtils.parseIfIsLong(id)) != null;
            }
        }
        return false;
    }

    private boolean validateRole(String role) {
        UserAccountDAO accountDAO = SpringUtils.getBean(UserAccountDAO.class);
        return accountDAO.getRoleById(role) != null;
    }

    private boolean validateRange(String effectiveRange) {
        if (StringUtils.isNotBlank(effectiveRange)) {
            String[] range = effectiveRange.split("-");
            if (range.length == 2) {
                Date start = DateUtils.parseDatetime(range[0]);
                Date end = DateUtils.parseDatetime(range[1]);
                if (start != null && end != null && start.before(end)) {
                    setActivatedAt(start);
                    setDeactivatedAt(end);
                    return true;
                }
            }
        }
        return false;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String[] getProject() {
        return project;
    }

    public void setProject(String[] project) {
        this.project = project;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getEffectiveRange() {
        return effectiveRange;
    }

    public void setEffectiveRange(String effectiveRange) {
        this.effectiveRange = effectiveRange;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(Date activatedAt) {
        this.activatedAt = activatedAt;
    }

    public Date getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(Date deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }
}
