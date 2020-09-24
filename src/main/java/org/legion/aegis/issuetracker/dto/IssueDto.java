package org.legion.aegis.issuetracker.dto;

import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.*;
import org.legion.aegis.common.validation.Length;
import org.legion.aegis.common.validation.MemberOf;
import org.legion.aegis.common.validation.NotBlank;
import org.legion.aegis.common.validation.ValidateWithMethod;
import org.legion.aegis.issuetracker.consts.IssueConsts;
import org.legion.aegis.issuetracker.controller.IssueController;
import org.legion.aegis.issuetracker.entity.Issue;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

public class IssueDto extends BaseDto {

    @ValidateWithMethod(methodName = "validateGroupId", message = "请选择项目组", profile = {"report"})
    private String groupId;
    @ValidateWithMethod(methodName = "validateProjectId", message = "请选择项目", profile = {"report"})
    private String projectId;
    @ValidateWithMethod(methodName = "validateModule", message = "请选择模块", profile = {"report"})
    private String moduleId;
    @NotBlank(message = "请输入对次此问题的概述")
    @Length(min = 5, max = 100, message = "概述不得少于5个字符，不得超过100字符", profile = {"report"})
    private String title;
    @ValidateWithMethod(methodName = "validateRpd", message = "请选择重现情况", profile = {"report"})
    private String reproducibility;
    @ValidateWithMethod(methodName = "validateStatus", message = "请选择问题状态", profile = {"update"})
    private String status;
    @ValidateWithMethod.List({
            @ValidateWithMethod(methodName = "validateResolution", message = "请选择解决状态", profile = {"update"}),
            @ValidateWithMethod(methodName = "validateMatchStatus", message = "所选解决状态与问题状态不符", profile = {"update"})
    })
    private String resolution;
    private String rootCause;
    @ValidateWithMethod(methodName = "validatePriority", message = "请选择优先级", profile = {"report", "update"})
    private String priority;
    @ValidateWithMethod(methodName = "validateSeverity", message = "请选择严重程度", profile = {"report", "update"})
    private String severity;
    @ValidateWithMethod(methodName = "validateAssignedTo", message = "请选择正确的用户", profile = {"report", "update"})
    private String assignedTo;
    @NotBlank(message = "请输入对此问题的详细描述", profile = {"report"})
    private String description;
    private String reportedBy;
    private Date reportedAt;

    @Length(max = 4000, message = "长度不能超过4000字符", profile = {"update"})
    private String updatedNote;

    @ValidateWithMethod(methodName = "validateFixedAt", message = "请输入正确的日期格式", profile = {"update"})
    private String fixedAt;

    @ValidateWithMethod(methodName = "validateConfirmedBy", message = "请选择需要等待确认的人员")
    private String confirmedBy;


    private List<MultipartFile> attachments;

    //additional
    private String issueId;

    private boolean validateGroupId(String groupId) {
        AppContext context = AppContext.getFromWebThread();
        Long id = StringUtils.parseIfIsLong(groupId);
        if (AppConsts.ROLE_SYSTEM_ADMIN.equals(context.getRoleId())) {
            ProjectService projectService = SpringUtils.getBean(ProjectService.class);
            return projectService.getProjectGroupById(id) != null;
        }
        List<UserProjectAssign> projectAssigns = context.getAssignments();
        if (projectAssigns != null) {
            for (UserProjectAssign assign : projectAssigns) {
                if (assign.getGroupId().equals(id)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validateProjectId(String projectId) {
        AppContext context = AppContext.getFromWebThread();
        ProjectService projectService = SpringUtils.getBean(ProjectService.class);
        ProjectGroup group = projectService.getProjectGroupById(StringUtils.parseIfIsLong(groupId));
        if (group != null) {
            Project project = projectService.getProjectById(StringUtils.parseIfIsLong(projectId), false);
            if (project != null && AppConsts.ROLE_SYSTEM_ADMIN.equals(context.getRoleId())) {
                return project.getGroupId().equals(group.getId());
            }
            List<UserProjectAssign> projectAssigns = context.getAssignments();
            if (projectAssigns != null && project != null) {
                for (UserProjectAssign assign : projectAssigns) {
                    if (UserAccountService.isSupervisor(context.getRoleId())) {
                        if (assign.getGroupId().equals(project.getGroupId())) {
                            return true;
                        }
                    } else {
                        if (assign.getProjectId().equals(project.getId())) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private boolean validateModule(String moduleId) {
        ProjectService projectService = SpringUtils.getBean(ProjectService.class);
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(projectId), true);
        if (project != null && project.getModules() != null) {
            for (Module module : project.getModules()) {
                if (module.getId().equals(StringUtils.parseIfIsLong(moduleId))) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validateRpd(String reproducibility) {
        String[] codes = MasterCodeUtils.getCodeArrayByType("issue.reproducibility");
        return ArrayUtils.contains(codes, reproducibility);
    }

    private boolean validateSeverity(String severity) {
        String[] codes = MasterCodeUtils.getCodeArrayByType("issue.severity");
        return ArrayUtils.contains(codes, severity);
    }

    private boolean validatePriority(String priority) {
        String[] codes = MasterCodeUtils.getCodeArrayByType("issue.priority");
        return ArrayUtils.contains(codes, priority);
    }

    private boolean validateStatus(String status) {
        SystemMgrService systemMgrService = SpringUtils.getBean(SystemMgrService.class);
        return systemMgrService.getIssueStatusByCode(status) != null;
    }

    private boolean validateResolution(String resolution) {
        SystemMgrService systemMgrService = SpringUtils.getBean(SystemMgrService.class);
        return systemMgrService.getIssueResolutionByCode(resolution) != null;
    }

    private boolean validateFixedAt(String fixedAt) {
        if (StringUtils.isNotBlank(fixedAt)) {
            return DateUtils.parseDatetime(fixedAt, "yyyy/MM/dd HH:mm") != null;
        }
        return true;
    }

    private boolean validateConfirmation(String confirmedBy) {
        if (IssueConsts.ISSUE_STATUS_PENDING_CONFIRMATION.equals(status)) {
            Issue issue = (Issue) SessionManager.getAttribute(IssueController.SESSION_KEY);
            if (issue != null) {
                UserAccountService service = SpringUtils.getBean(UserAccountService.class);
                List<UserAccount> developers = service.getDevelopersUnderProject(issue.getProjectId());
                List<UserAccount> reporters = service.getReportersUnderProject(issue.getProjectId());
                developers.addAll(reporters);
                developers.removeIf(var -> var.getId().equals(issue.getAssignedTo()));
                for (UserAccount user : developers) {
                    if (user.getId().equals(StringUtils.parseIfIsLong(confirmedBy))) {
                        return true;
                    }
                }
            }
            return false;
        }
        return true;
    }

    private boolean validateMatchStatus(String res) {
        if (StringUtils.isNotBlank(res)) {
            if (IssueConsts.ISSUE_STATUS_CLOSED.equals(status)) {
                return res.equals(IssueConsts.ISSUE_RESOLUTION_NO_NEED)
                        || res.equals(IssueConsts.ISSUE_RESOLUTION_RESOLVED)
                        || res.equals(IssueConsts.ISSUE_RESOLUTION_UNSOLVABLE);
            } else if (IssueConsts.ISSUE_STATUS_REOPEN.equals(status)) {
                return !res.equals(IssueConsts.ISSUE_RESOLUTION_RESOLVED);
            }
            return true;
        }
        return false;
    }

    private boolean validateAssignedTo(String assignedTo) {
        if (StringUtils.isNotBlank(assignedTo) && !"0". equals(assignedTo)) {
            Long projectId = null;
            Issue issue = (Issue) SessionManager.getAttribute(IssueController.SESSION_KEY);
            if (issue != null) {
                projectId = issue.getProjectId();
            } else {
                projectId = StringUtils.parseIfIsLong(this.projectId);
            }
            UserAccountService userAccountService = SpringUtils.getBean(UserAccountService.class);
            List<UserAccount> developers = userAccountService.getDevelopersUnderProject(projectId);
            List<UserAccount> reporters = userAccountService.getReportersUnderProject(projectId);
            developers.addAll(reporters);
            for (UserAccount user : developers) {
                if (user.getId().equals(Long.parseLong(assignedTo))) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    private boolean validateConfirmedBy(String confirmedBy) {
        if (IssueConsts.ISSUE_STATUS_PENDING_CONFIRMATION.equals(status) && StringUtils.isBlank(confirmedBy)) {
            return false;
        } else if (IssueConsts.ISSUE_STATUS_PENDING_CONFIRMATION.equals(status)) {
            return validateAssignedTo(confirmedBy);
        } else {
            this.confirmedBy = null;
        }
        return true;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public Date getReportedAt() {
        return reportedAt;
    }

    public void setReportedAt(Date reportedAt) {
        this.reportedAt = reportedAt;
    }

    public List<MultipartFile> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<MultipartFile> attachments) {
        this.attachments = attachments;
    }

    public String getUpdatedNote() {
        return updatedNote;
    }

    public void setUpdatedNote(String updatedNote) {
        this.updatedNote = updatedNote;
    }

    public String getFixedAt() {
        return fixedAt;
    }

    public void setFixedAt(String fixedAt) {
        this.fixedAt = fixedAt;
    }

    public String getConfirmedBy() {
        return confirmedBy;
    }

    public void setConfirmedBy(String confirmedBy) {
        this.confirmedBy = confirmedBy;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }
}
