package org.legion.aegis.issuetracker.controller;

import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.webmvc.NetworkFileTransfer;
import org.legion.aegis.general.entity.FileNet;
import org.legion.aegis.general.service.FileNetService;
import org.legion.aegis.issuetracker.dto.ExportDto;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.entity.IssueAttachment;
import org.legion.aegis.issuetracker.service.IssueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
public class AttachmentDlController {

    private final FileNetService fileNetService;
    private final ProjectService projectService;
    private final IssueService issueService;

    private static final Logger log = LoggerFactory.getLogger(AttachmentDlController.class);

    public AttachmentDlController(FileNetService fileNetService, ProjectService projectService, IssueService issueService) {
        this.fileNetService = fileNetService;
        this.projectService = projectService;
        this.issueService = issueService;
    }

    @GetMapping("/web/issue/download/{uuid}")
    @RequiresLogin
    public void downloadAttachment(@PathVariable("uuid") String uuid, HttpServletResponse response) throws Exception {
        Issue issue = (Issue) SessionManager.getAttribute(IssueController.SESSION_KEY);
        if (issue != null) {
            Project project = projectService.getProjectById(issue.getProjectId(), false);
            List<IssueAttachment> attachments = issueService.getIssueAttachment(issue.getId());
            for (IssueAttachment attachment : attachments) {
                FileNet fileNet = fileNetService.getFileNetById(attachment.getFileNetId());
                if (fileNet != null && fileNet.getFileUuid().equals(uuid)) {
                    NetworkFileTransfer.download(SystemConsts.ROOT_STORAGE_PATH + project.getFilePath()
                            + File.separator + fileNet.getFileUuid(), fileNet.getFileName(), response);
                    log.info("Attachment Downloaded -> [" + fileNet.getFileUuid() + "]");
                    break;
                }
            }
        }
    }

    @GetMapping("/web/issue/export/download/{uuid}")
    @RequiresLogin
    public void export(@PathVariable("uuid") String uuid, HttpServletResponse response) throws Exception {
        ExportDto exportDto = (ExportDto) SessionManager.getAttribute(IssueController.SESSION_DL_KEY);
        if (exportDto != null) {
            if (StringUtils.isNotBlank(uuid) && uuid.equals(UUID.nameUUIDFromBytes(exportDto.getData()).toString())) {
                String date = DateUtils.getDateString(new Date(), DateUtils.TODAY_FORMAT);
                NetworkFileTransfer.download(exportDto.getData(), "Export-" + date + "." + exportDto.getType() , response);
                AppContext context = AppContext.getFromWebThread();
                log.info("Issue Export Downloaded -> [" + context.getLoginId() +" : " + uuid +"]");
            }
        }
        SessionManager.removeAttribute(IssueController.SESSION_DL_KEY);
    }

    @GetMapping("/web/issue/statistics/report/download/{uuid}")
    @RequiresLogin
    public void exportStatistics(@PathVariable("uuid") String uuid, HttpServletResponse response) throws Exception {
        ExportDto exportDto = (ExportDto) SessionManager.getAttribute(IssueStatisticsController.SESSION_DL_KEY);
        if (exportDto != null) {
            if (StringUtils.isNotBlank(uuid) && uuid.equals(UUID.nameUUIDFromBytes(exportDto.getData()).toString())) {
                String date = DateUtils.getDateString(new Date(), DateUtils.TODAY_FORMAT);
                NetworkFileTransfer.download(exportDto.getData(), "StatisticReport-" + date + "." + exportDto.getType() , response);
                AppContext context = AppContext.getFromWebThread();
                log.info("Issue Statistics Report Downloaded -> [" + context.getLoginId() +" : " + uuid +"]");
            }
        }
        SessionManager.removeAttribute(IssueStatisticsController.SESSION_DL_KEY);
    }
}
