package org.legion.aegis.admin.dto;

import org.legion.aegis.admin.dao.ProjectDAO;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.utils.ValidationUtils;
import org.legion.aegis.common.validation.ValidateWithMethod;
import org.legion.aegis.common.validation.ValidateWithRegex;

import java.util.Arrays;

public class UserDto extends BaseDto {

    @ValidateWithRegex(regex = ValidationUtils.LOGIN_ID_REGEX, message = "用户名只能包含数字，字母和下划线，长度4~16位")
    private String loginId;
    @ValidateWithRegex(regex = ValidationUtils.SINGLE_EMAIL_REGEX, message = "请检查邮箱地址的格式")
    private String email;
    @ValidateWithMethod(method = "validateProject", message = "请添加正确的项目")
    private String[] project;
    private String role;
    private String effectiveRange;
    private String password;

    private boolean validateProject(String[] project) {
        if (project != null) {
            ProjectDAO projectDAO = SpringUtils.getBean(ProjectDAO.class);
            for (String id : project) {
                return projectDAO.getProjectById(StringUtils.parseIfIsLong(id)) != null;
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
}
