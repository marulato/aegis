package org.legion.aegis.issuetracker.entity;

import org.legion.aegis.common.base.BasePO;
import org.legion.aegis.common.jpa.annotation.Entity;

@Entity(tableName = "ISU_SEARCH_FILTER", whereClause = "USER_ACCT_ID = #{userAcctId}")
public class SearchFilter extends BasePO {

    private Long userAcctId;
    private Long groupId;
    private Long projectId;
    private Long reporter;
    private Long developer;
    private Long moduleId;
    private String severity;
    private String status;
    private String resolution;

    public Long getUserAcctId() {
        return userAcctId;
    }

    public void setUserAcctId(Long userAcctId) {
        this.userAcctId = userAcctId;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public Long getReporter() {
        return reporter;
    }

    public void setReporter(Long reporter) {
        this.reporter = reporter;
    }

    public Long getDeveloper() {
        return developer;
    }

    public void setDeveloper(Long developer) {
        this.developer = developer;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}
