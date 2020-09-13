package org.legion.aegis.issuetracker.controller;

import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.legion.aegis.issuetracker.dto.IssueDto;
import org.legion.aegis.issuetracker.entity.Issue;
import org.legion.aegis.issuetracker.entity.IssueNote;
import org.legion.aegis.issuetracker.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class IssueController {

    private final IssueService issueService;
    private final ProjectService projectService;
    private final UserAccountService userAccountService;
    private final SystemMgrService systemMgrService;

    public static final String SESSION_KEY = "SESSION_ISSUE";

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
        SessionManager.removeAttribute(SESSION_KEY);
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
            List<UserAccount> reporters = userAccountService.getReportersUnderProject(StringUtils.parseIfIsLong(parentId));
            UserAccount account = new UserAccount();
            account.setId(0L);
            account.setName("- 请选择 -");
            reporters.add(0, account);
            manager.addDataObjects(reporters);
        } else if ("developer".equals(type)) {
            List<UserAccount> developers = userAccountService.getDevelopersUnderProject(StringUtils.parseIfIsLong(parentId));
            UserAccount account = new UserAccount();
            account.setId(0L);
            account.setName("- 请选择 -");
            developers.add(0, account);
            manager.addDataObjects(developers);
        } else if ("module".equals(type)) {
            List<Module> modules = projectService.getModulesByProjectId(StringUtils.parseIfIsLong(parentId));
            Module module = new Module();
            module.setId(0L);
            module.setName("- 请选择 -");
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
        SessionManager.removeAttribute(SESSION_KEY);
        AppContext context = AppContext.getFromWebThread();
        ModelAndView modelAndView = main();
        modelAndView.setViewName("issue/reportIssue");
        modelAndView.addObject("rpd", MasterCodeUtils.getMasterCodeByType("issue.reproducibility"));
        modelAndView.addObject("priority", MasterCodeUtils.getMasterCodeByType("issue.priority"));
        return modelAndView;
    }

    @PostMapping("/web/issue/report")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody reportIssue(IssueDto dto, HttpServletRequest request) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
        List<ConstraintViolation> violations = CommonValidator.validate(dto, "report");
        dto.setAttachments(req.getFiles("attachments"));
        if (!violations.isEmpty()) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(violations);
        } else {
            issueService.reportIssue(dto);
        }
        return manager.respond();
    }

    @GetMapping("/web/issue/view/{id}")
    @RequiresLogin
    public ModelAndView viewIssue(@PathVariable("id") String id) {
        AppContext context = AppContext.getFromWebThread();
        ModelAndView modelAndView = new ModelAndView("issue/viewIssue");
        Issue issue = issueService.getIssueById(StringUtils.parseIfIsLong(id));
        modelAndView.addObject("role", context.getRoleId());
        modelAndView.addObject("issue", issueService.getIssueVOForView(issue));
        if (issue != null) {
            List<UserAccount> developers = userAccountService.getDevelopersUnderProject(issue.getProjectId());
            List<UserAccount> reporters = userAccountService.getReportersUnderProject(issue.getProjectId());
            developers.addAll(reporters);
            developers.removeIf(var -> var.getId().equals(issue.getAssignedTo()));
            modelAndView.addObject("assignedTo", developers);
            modelAndView.addObject("status", systemMgrService.getAllInuseStatus());
            modelAndView.addObject("resolution", systemMgrService.getAllInuseResolutions());
            modelAndView.addObject("severity", MasterCodeUtils.getMasterCodeByType("issue.severity"));
            modelAndView.addObject("priority", MasterCodeUtils.getMasterCodeByType("issue.priority"));
            modelAndView.addObject("timeline", issueService.retrieveIssueHistory(issue.getId()));
        }
        SessionManager.setAttribute(SESSION_KEY, issue);
        return modelAndView;
    }

    @PostMapping("/web/issue/addNote")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody addNote(String content) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Issue issue = (Issue) SessionManager.getAttribute(SESSION_KEY);
        if (StringUtils.isBlank(content) || content.length() > 4000) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addError("note", "内容不能为空且长度不超过4000字符");
        } else {
            IssueNote issueNote = new IssueNote();
            issueNote.setIssueId(issue.getId());
            issueNote.setContent(content);
            JPAExecutor.save(issueNote);
        }
        return manager.respond();
    }

    @PostMapping("/web/issue/update")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody updateIssue(IssueDto dto, HttpServletRequest request) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        StandardMultipartHttpServletRequest req = (StandardMultipartHttpServletRequest) request;
        Issue issue = (Issue) SessionManager.getAttribute(SESSION_KEY);
        List<ConstraintViolation> violations = CommonValidator.validate(dto, "update");
        dto.setAttachments(req.getFiles("attachments"));
        if (!violations.isEmpty()) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(violations);
        } else {
            issueService.updateIssue(dto, issue.getId());
        }
        return manager.respond();
    }

    @PostMapping("/web/issue/assign")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody reassign(String assignedTo) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Issue issue = (Issue) SessionManager.getAttribute(SESSION_KEY);
        if (issue != null) {
            List<UserAccount> developers = userAccountService.getDevelopersUnderProject(issue.getProjectId());
            List<UserAccount> reporters = userAccountService.getReportersUnderProject(issue.getProjectId());
            developers.addAll(reporters);
            for (UserAccount user : developers) {
                if (user.getId().equals(StringUtils.parseIfIsLong(assignedTo))) {
                    issueService.reAssign(issue.getId(), user.getId());
                    break;
                }
            }
        }
        return manager.respond();
    }

}
