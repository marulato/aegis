package org.legion.aegis.admin.vo;

import org.legion.aegis.common.base.BaseVO;

import java.io.Serializable;
import java.util.Date;

public class UserSearchVO extends BaseVO {

    private Long id;
    private String loginId;
    private String email;
    private String name;
    private String status;
    private String lastLoginSuccessDt;
    private String activatedAt;
    private String deactivatedAt;
    private String role;
    private String roleId;
    private String project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastLoginSuccessDt() {
        return lastLoginSuccessDt;
    }

    public void setLastLoginSuccessDt(String lastLoginSuccessDt) {
        this.lastLoginSuccessDt = lastLoginSuccessDt;
    }

    public String getActivatedAt() {
        return activatedAt;
    }

    public void setActivatedAt(String activatedAt) {
        this.activatedAt = activatedAt;
    }

    public String getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(String deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getProject() {
        return project;
    }

    public void setProject(String project) {
        this.project = project;
    }

}
