package org.legion.aegis.issuetracker.controller;

import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.webmvc.NetworkFileTransfer;
import org.legion.aegis.general.entity.FileNet;
import org.legion.aegis.general.service.FileNetService;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.entity.IssueAttachment;
import org.legion.aegis.issuetracker.service.IssueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.List;

@Controller
public class AttachmentDlController {

    private final FileNetService fileNetService;
    private final ProjectService projectService;
    private final IssueService issueService;

    public AttachmentDlController(FileNetService fileNetService, ProjectService projectService, IssueService issueService) {
        this.fileNetService = fileNetService;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    @GetMapping("/web/issue/download/{uuid}")
    public void download(@PathVariable("uuid") String uuid, HttpServletResponse response) throws Exception {
        Issue issue = (Issue) SessionManager.getAttribute(IssueController.SESSION_KEY);
        if (issue != null) {
            Project project = projectService.getProjectById(issue.getProjectId(), false);
            List<IssueAttachment> attachments = issueService.getIssueAttachment(issue.getId());
            for (IssueAttachment attachment : attachments) {
                FileNet fileNet = fileNetService.getFileNetById(attachment.getFileNetId());
                if (fileNet != null && fileNet.getFileUuid().equals(uuid)) {
                    NetworkFileTransfer.download(SystemConsts.ROOT_STORAGE_PATH + project.getFilePath()
                            + File.separator + fileNet.getFileUuid(), fileNet.getFileName(), response);
                    break;
                }
            }
        }
    }
}
