package org.legion.aegis.issuetracker.controller;

import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.issuetracker.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
public class IssueController {

    private final IssueService issueService;
    private final ProjectService projectService;
    private final UserAccountService userAccountService;
    private final SystemMgrService systemMgrService;

    @Autowired
    public IssueController(IssueService issueService, ProjectService projectService,
                           UserAccountService userAccountService, SystemMgrService systemMgrService) {
        this.issueService = issueService;
        this.projectService = projectService;
        this.userAccountService = userAccountService;
        this.systemMgrService = systemMgrService;
    }

    @PostMapping("/web/issue/view")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody search(@RequestBody SearchParam param) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext context = AppContext.getFromWebThread();
        param.addParam("roleId", context.getRoleId());
        param.addParam("userId", context.getUserId());
        manager.addDataObject(issueService.search(param));
        return manager.respond();
    }

    @GetMapping("/web/issue/view")
    @RequiresLogin
    public ModelAndView main() {
        AppContext context = AppContext.getFromWebThread();
        ModelAndView modelAndView = new ModelAndView("issue/dashboard");
        modelAndView.addObject("role", context.getRoleId());
        List<ProjectGroup> projectGroups = projectService.getProjectGroupUnderUser(context.getUserId(), context.getRoleId());
        modelAndView.addObject("groupSelector", projectGroups);
        modelAndView.addObject("status", systemMgrService.getAllInuseStatus());
        modelAndView.addObject("resolution", systemMgrService.getAllInuseResolutions());
        modelAndView.addObject("severity", MasterCodeUtils.getMasterCodeByType("issue.severity"));
        return modelAndView;
    }

    @GetMapping("/web/issue/selector/{type}/{parentId}")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody prepareSelector(@PathVariable("type") String type, @PathVariable("parentId") String parentId) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext context = AppContext.getFromWebThread();
        if ("project".equals(type)) {
            List<Project> projects = projectService.getAccessibleProjectsForUser(context, StringUtils.parseIfIsLong(parentId));
            manager.addDataObjects(projects);
        } else if ("reporter".equals(type)) {
            List<UserAccount> reporters = userAccountService.getReportersUnderGroup(StringUtils.parseIfIsLong(parentId));
            UserAccount account = new UserAccount();
            account.setId(null);
            account.setName("- 全部 -");
            reporters.add(0, account);
            manager.addDataObjects(reporters);
        } else if ("developer".equals(type)) {
            List<UserAccount> developers = userAccountService.getDevelopersUnderGroup(StringUtils.parseIfIsLong(parentId));
            UserAccount account = new UserAccount();
            account.setId(null);
            account.setName("- 全部 -");
            developers.add(0, account);
            manager.addDataObjects(developers);
        } else if ("module".equals(type)) {
            List<Module> modules = projectService.getModulesByProjectId(StringUtils.parseIfIsLong(parentId));
            Module module = new Module();
            module.setId(null);
            module.setName("- 全部 -");
            if (modules.isEmpty()) {
                modules.add(module);
            } else {
                modules.add(0, module);
            }
            manager.addDataObjects(modules);
        }
        return manager.respond();
    }

    @GetMapping("/web/issue/report")
    @RequiresLogin
    public ModelAndView reportIssuePage() {
        AppContext context = AppContext.getFromWebThread();
        ModelAndView modelAndView = main();
        modelAndView.setViewName("issue/reportIssue");
        modelAndView.addObject("rpd", MasterCodeUtils.getMasterCodeByType("issue.reproducibility"));
        modelAndView.addObject("priority", MasterCodeUtils.getMasterCodeByType("issue.priority"));
        return modelAndView;
    }
}
