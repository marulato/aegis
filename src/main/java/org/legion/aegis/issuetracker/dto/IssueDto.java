package org.legion.aegis.issuetracker.dto;

import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.admin.entity.UserProjectAssign;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.BaseDto;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.ArrayUtils;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.validation.MemberOf;
import org.legion.aegis.common.validation.NotBlank;
import org.legion.aegis.common.validation.ValidateWithMethod;

import java.util.Date;
import java.util.List;

public class IssueDto extends BaseDto {

    @ValidateWithMethod(methodName = "validateGroupId", message = "请选择项目组")
    private Long groupId;
    @ValidateWithMethod(methodName = "validateProjectId", message = "请选择项目")
    private Long projectId;
    @ValidateWithMethod(methodName = "validateModule", message = "请选择模块")
    private Long moduleId;
    @NotBlank(message = "请输入对次此问题的概述")
    private String title;
    @ValidateWithMethod(methodName = "validateRpd", message = "请选择重现情况")
    private String reproducibility;
    private String status;
    private String resolution;
    private String rootCause;
    private String priority;
    private String severity;
    private Long assignedTo;
    @NotBlank(message = "请输入对此问题的详细描述")
    private String description;
    private Long reportedBy;
    private Date reportedAt;

    private boolean validateGroupId(Long groupId) {
        AppContext context = AppContext.getFromWebThread();
        if (AppConsts.ROLE_SYSTEM_ADMIN.equals(context.getRoleId())) {
            ProjectService projectService = SpringUtils.getBean(ProjectService.class);
            return projectService.getProjectGroupById(groupId) != null;
        }
        List<UserProjectAssign> projectAssigns = context.getAssignments();
        if (projectAssigns != null) {
            for (UserProjectAssign assign : projectAssigns) {
                if (assign.getGroupId().equals(groupId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean validateProjectId(Long projectId) {
        AppContext context = AppContext.getFromWebThread();
        ProjectService projectService = SpringUtils.getBean(ProjectService.class);
        ProjectGroup group = projectService.getProjectGroupById(groupId);
        if (group != null) {
            Project project = projectService.getProjectById(projectId, false);
            if (project != null && AppConsts.ROLE_SYSTEM_ADMIN.equals(context.getRoleId())) {
                return project.getGroupId().equals(group.getId());
            }
            List<UserProjectAssign> projectAssigns = context.getAssignments();
            if (projectAssigns != null && project != null) {
                for (UserProjectAssign assign : projectAssigns) {
                    if (assign.getProjectId().equals(project.getId())) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean validateModule(Long moduleId) {
        ProjectService projectService = SpringUtils.getBean(ProjectService.class);
        Project project = projectService.getProjectById(projectId, true);
        if (project != null && project.getModules() != null) {
            for (Module module : project.getModules()) {
                if (module.getId().equals(moduleId)) {
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

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
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

    public Long getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(Long assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
}
