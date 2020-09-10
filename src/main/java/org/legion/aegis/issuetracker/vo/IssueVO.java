package org.legion.aegis.issuetracker.vo;

import org.legion.aegis.common.base.BaseVO;
import java.util.Date;

public class IssueVO extends BaseVO {

    private Long id;
    private Long groupIssueId;
    private Long moduleId;
    private Long groupId;
    private Long projectId;
    private String title;
    private byte[] description;
    private String reproducibility;
    private String status;
    private String resolution;
    private Integer priority;
    private Integer severity;
    private Long assignedTo;
    private String rootCause;
    private Date fixedAt;
    private Long fixedBy;
    private Long reportedBy;
    private Date reportedAt;
    private String tag;

    private String priorityDesc;
    private String severityDesc;
    private String assignedToDesc;
    private String projectName;
    private String groupName;
    private String moduleName;
    private String reportedByDesc;
    private String reportedAtDesc;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupIssueId() {
        return groupIssueId;
    }

    public void setGroupIssueId(Long groupIssueId) {
        this.groupIssueId = groupIssueId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public byte[] getDescription() {
        return description;
    }

    public void setDescription(byte[] description) {
        this.description = description;
    }

    public String getReproducibility() {
        return reproducibility;
    }

    public void setReproducibility(String reproducibility) {
        this.reproducibility = reproducibility;
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

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Integer getSeverity() {
        return severity;
    }

    public void setSeverity(Integer severity) {
        this.severity = severity;
    }

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public Date getFixedAt() {
        return fixedAt;
    }

    public void setFixedAt(Date fixedAt) {
        this.fixedAt = fixedAt;
    }

    public Long getFixedBy() {
        return fixedBy;
    }

    public void setFixedBy(Long fixedBy) {
        this.fixedBy = fixedBy;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getPriorityDesc() {
        return priorityDesc;
    }

    public void setPriorityDesc(String priorityDesc) {
        this.priorityDesc = priorityDesc;
    }

    public String getSeverityDesc() {
        return severityDesc;
    }

    public void setSeverityDesc(String severityDesc) {
        this.severityDesc = severityDesc;
    }

    public String getAssignedToDesc() {
        return assignedToDesc;
    }

    public void setAssignedToDesc(String assignedToDesc) {
        this.assignedToDesc = assignedToDesc;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public Long getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(Long reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Date getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(Date reportedAt) {
        this.reportedAt = reportedAt;
    }

    public String getReportedByDesc() {
        return reportedByDesc;
    }

    public void setReportedByDesc(String reportedByDesc) {
        this.reportedByDesc = reportedByDesc;
    }

    public String getReportedAtDesc() {
        return reportedAtDesc;
    }

    public void setReportedAtDesc(String reportedAtDesc) {
        this.reportedAtDesc = reportedAtDesc;
    }
}
