package org.legion.aegis.issuetracker.vo;

import org.legion.aegis.common.base.BaseVO;
import org.legion.aegis.general.entity.FileNet;
import org.legion.aegis.issuetracker.entity.IssueConfirmation;
import org.legion.aegis.issuetracker.entity.IssueFollower;
import org.legion.aegis.issuetracker.entity.IssueNote;

import java.util.Date;
import java.util.List;

public class IssueVO extends BaseVO {

    private Long id;
    private Long groupIssueId;
    private Long moduleId;
    private Long groupId;
    private Long projectId;
    private String title;
    private String reproducibility;
    private String status;
    private String resolution;
    private String rootCause;
    private String priority;
    private String severity;
    private String assignedTo;
    private String projectName;
    private String groupName;
    private String moduleName;
    private String reportedBy;
    private String reportedAt;
    private String sla;

    private String color;
    private String issueId;
    private String description;
    private String severityColor;

    private String statusCode;
    private String severityCode;
    private String priorityCode;
    private String resolutionCode;

    private List<FileNet> attachments;
    private List<IssueNoteVO> notes;
    private List<String> followers;
    private List<IssueRelationshipVO> relationships;
    private List<IssueVcsTrackerVO> vcsTrackers;

    private String fixedAt;

    private IssueConfirmationVO confirmation;

    private boolean canConfirm;
    private boolean canCancel;

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

    public String getRootCause() {
        return rootCause;
    }

    public void setRootCause(String rootCause) {
        this.rootCause = rootCause;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
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

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(String reportedAt) {
        this.reportedAt = reportedAt;
    }

    public String getSla() {
        return sla;
    }

    public void setSla(String sla) {
        this.sla = sla;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<FileNet> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<FileNet> attachments) {
        this.attachments = attachments;
    }

    public List<IssueNoteVO> getNotes() {
        return notes;
    }

    public void setNotes(List<IssueNoteVO> notes) {
        this.notes = notes;
    }

    public String getSeverityColor() {
        return severityColor;
    }

    public void setSeverityColor(String severityColor) {
        this.severityColor = severityColor;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getSeverityCode() {
        return severityCode;
    }

    public void setSeverityCode(String severityCode) {
        this.severityCode = severityCode;
    }

    public String getPriorityCode() {
        return priorityCode;
    }

    public void setPriorityCode(String priorityCode) {
        this.priorityCode = priorityCode;
    }

    public String getResolutionCode() {
        return resolutionCode;
    }

    public void setResolutionCode(String resolutionCode) {
        this.resolutionCode = resolutionCode;
    }

    public String getFixedAt() {
        return fixedAt;
    }

    public void setFixedAt(String fixedAt) {
        this.fixedAt = fixedAt;
    }

    public IssueConfirmationVO getConfirmation() {
        return confirmation;
    }

    public void setConfirmation(IssueConfirmationVO confirmation) {
        this.confirmation = confirmation;
    }

    public boolean isCanConfirm() {
        return canConfirm;
    }

    public void setCanConfirm(boolean canConfirm) {
        this.canConfirm = canConfirm;
    }

    public boolean isCanCancel() {
        return canCancel;
    }

    public void setCanCancel(boolean canCancel) {
        this.canCancel = canCancel;
    }

    public List<String> getFollowers() {
        return followers;
    }

    public void setFollowers(List<String> followers) {
        this.followers = followers;
    }

    public List<IssueRelationshipVO> getRelationships() {
        return relationships;
    }

    public void setRelationships(List<IssueRelationshipVO> relationships) {
        this.relationships = relationships;
    }

    public List<IssueVcsTrackerVO> getVcsTrackers() {
        return vcsTrackers;
    }

    public void setVcsTrackers(List<IssueVcsTrackerVO> vcsTrackers) {
        this.vcsTrackers = vcsTrackers;
    }
}
