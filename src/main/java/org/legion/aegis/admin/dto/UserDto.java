package org.legion.aegis.admin.dto;

import org.legion.aegis.admin.dao.ProjectDAO;
import org.legion.aegis.admin.dao.UserAccountDAO;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;
import org.legion.aegis.common.validation.Length;
import org.legion.aegis.common.validation.MatchesPattern;
import org.legion.aegis.common.validation.NotBlank;
import org.legion.aegis.common.validation.ValidateWithMethod;

import java.util.Date;

public class UserDto extends BaseDto {

    @MatchesPattern(pattern = ValidationUtils.LOGIN_ID_REGEX, message = "用户名只能包含数字，字母和下划线，长度4~16位")
    @ValidateWithMethod(methodName = "validateLoginId", message = "改账号名称已被使用")
    private String loginId;
    @MatchesPattern(pattern = ValidationUtils.SINGLE_EMAIL_REGEX, message = "请输入正确的邮件地址")
    private String email;
    @ValidateWithMethod(methodName = "validateProject", message = "请添加正确的项目")
    private String[] project;
    @ValidateWithMethod(methodName = "validateRole", message = "请选择正确的权限")
    private String role;
    @ValidateWithMethod(methodName = "validateRange", message = "请选择正确的日期范围")
    private String effectiveRange;
    private String password;

    @NotBlank(message = "名字不能为空")
    @Length(min = 3, max = 32, message = "名字长度为3~32字符")
    private String name;

    private Date activatedAt;
    private Date deactivatedAt;

    private boolean validateLoginId(String loginId) {
        UserAccountService accountService = SpringUtils.getBean(UserAccountService.class);
        UserAccount account = accountService.getUserByLoginId(loginId);
        return account == null;
    }

    private boolean validateProject(String[] project) {
        if (project != null && project.length > 0) {
            ProjectService projectService = SpringUtils.getBean(ProjectService.class);
            if (AppConsts.ROLE_DEV_SUPERVISOR.equals(role) || AppConsts.ROLE_QA_SUPERVISOR.equals(role)) {
                for (String groupId : project) {
                    ProjectGroup group = projectService.getProjectGroupById(StringUtils.parseIfIsLong(groupId));
                    if (group == null) {
                        return false;
                    }
                }
            } else if (AppConsts.ROLE_DEV.equals(role) || AppConsts.ROLE_QA.equals(role)) {
                for (String projectId : project) {
                    Project var = projectService.getProjectById(StringUtils.parseIfIsLong(projectId), false);
                    if (var == null) {
                        return false;
                    }
                }
            }
            return true;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
