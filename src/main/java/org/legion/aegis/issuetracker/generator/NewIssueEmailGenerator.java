package org.legion.aegis.issuetracker.generator;

import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.docgen.EmailTemplateGenerator;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.issuetracker.entity.Issue;

import java.util.HashMap;
import java.util.Map;

public class NewIssueEmailGenerator extends EmailTemplateGenerator {

    private final Issue issue;

    public NewIssueEmailGenerator(Issue issue) {
        this.issue = issue;
    }

    @Override
    public String getSubject() {
        return "新问题已分配: <AEGIS ID: " + issue.getId() + ">";
    }

    @Override
    public Map<String, Object> getParameters() {
        Map<String, Object> params = new HashMap<>();
        UserAccountService userAccountService = SpringUtils.getBean(UserAccountService.class);
        ProjectService projectService = SpringUtils.getBean(ProjectService.class);
        SystemMgrService systemMgrService = SpringUtils.getBean(SystemMgrService.class);
        UserAccount user = userAccountService.getUserById(issue.getAssignedTo());
        Project project = projectService.getProjectById(issue.getProjectId(), true);
        ProjectGroup group = projectService.getProjectGroupById(issue.getGroupId());
        params.put("issueId", issue.getId());
        params.put("name", user.getName());
        params.put("groupName", group.getName());
        params.put("projectName", project.getName());
        for (Module module : project.getModules()) {
            if (module.getId().equals(issue.getModuleId())) {
                params.put("moduleName", module.getName());
                break;
            }
        }
        MasterCode severity = MasterCodeUtils.getMasterCode("issue.severity", issue.getSeverity());
        IssueStatus status = systemMgrService.getIssueStatusByCode(issue.getStatus());
        params.put("status", status.getDisplayName());
        params.put("resolution", systemMgrService.getIssueResolutionByCode(issue.getResolution()).getDisplayName());
        params.put("severity", severity.getValue());
        params.put("priority", MasterCodeUtils.getMasterCode("issue.priority", issue.getPriority()).getValue());
        params.put("reportedBy", userAccountService.getUserById(issue.getReportedBy()).getName());
        params.put("reportedAt", DateUtils.getDateString(issue.getReportedAt(), DateUtils.STD_FORMAT_2));
        params.put("title", issue.getTitle());
        params.put("color", status.getColor());
        params.put("severityColor", StringUtils.isBlank(severity.getDescription()) ? "#000000" : severity);

        return params;
    }

    @Override
    public String getTemplate() {
        return "newIssueEmail.ftl";
    }
}
