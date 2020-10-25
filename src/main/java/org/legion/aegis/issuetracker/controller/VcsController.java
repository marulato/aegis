package org.legion.aegis.issuetracker.controller;

import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.aop.permission.RequiresRoles;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.issuetracker.dto.ExportDto;
import org.legion.aegis.issuetracker.generator.IssueVCSGenerator;
import org.legion.aegis.issuetracker.service.IssueService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.UUID;

@Controller
public class VcsController {

    private final IssueService issueService;
    private final ProjectService projectService;

    public static final String SESSION_DL_KEY = "ISSUE_VCS_DL";

    public VcsController(IssueService issueService, ProjectService projectService) {
        this.issueService = issueService;
        this.projectService = projectService;
    }

    @GetMapping("/web/issue/versionControl")
    @RequiresRoles({AppConsts.ROLE_DEV_SUPERVISOR, AppConsts.ROLE_SYSTEM_ADMIN})
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView("issue/versionControl");
        modelAndView.addObject("projects", projectService.
                getAccessibleProjectsForUser(AppContext.getFromWebThread(), null));
        modelAndView.addObject("role", AppContext.getFromWebThread().getRoleId());
        return modelAndView;
    }

    @PostMapping("/web/issue/versionControl")
    @RequiresRoles({AppConsts.ROLE_DEV_SUPERVISOR, AppConsts.ROLE_SYSTEM_ADMIN})
    @ResponseBody
    public AjaxResponseBody search(@RequestBody SearchParam param) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(issueService.searchIssueVCS(param));
        return manager.respond();
    }

    @PostMapping("/web/issue/versionControl/export")
    @RequiresRoles({AppConsts.ROLE_DEV_SUPERVISOR, AppConsts.ROLE_SYSTEM_ADMIN})
    @ResponseBody
    public AjaxResponseBody export(@RequestBody SearchParam param) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        param.addParam("export", AppConsts.YES);
        IssueVCSGenerator generator = new IssueVCSGenerator(issueService.searchIssueVCS(param).getResultList());
        ExportDto exportDto = new ExportDto();
        exportDto.setType("xlsx");
        exportDto.setData(generator.generate());
        exportDto.setUuid(UUID.nameUUIDFromBytes(exportDto.getData()).toString());
        SessionManager.setAttribute(SESSION_DL_KEY, exportDto);
        manager.addDataObject("/web/issue/versionControl/download/" + exportDto.getUuid());
        return manager.respond();
    }
}
