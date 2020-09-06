package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.dto.ProjectDto;
import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.validator.ModuleValidator;
import org.legion.aegis.admin.vo.ProjectGroupVO;
import org.legion.aegis.admin.vo.ProjectVO;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.aop.permission.RequiresRoles;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.exception.RecordsNotFoundException;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Controller
public class ProjectMgrController {

    private final ProjectService projectService;
    public static final String SESSION_KEY = "SESSION_PROJECT";

    @Autowired
    public ProjectMgrController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/web/project")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView getProjectPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/projectList");
        AppContext context = AppContext.getAppContext(request);
        modelAndView.addObject("role", context.getRoleId());
        modelAndView.addObject("groupList", projectService.
                getProjectGroupUnderUser(context.getUserId(), context.getCurrentRole().getId()));
        SessionManager.removeAttribute(SESSION_KEY);
        return modelAndView;
    }

    @GetMapping("/web/projectGroup")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public ModelAndView getProjectGroupPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/projectGroupList");
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @GetMapping("/web/project/{id}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView display(@PathVariable("id") String id, HttpServletRequest request) {
        projectService.verifyRequest(StringUtils.parseIfIsLong(id), AppContext.getAppContext(request));
        ModelAndView modelAndView = new ModelAndView("admin/projectDisplay");
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), true);
        ProjectGroup projectGroup = projectService.getProjectGroupById(project.getGroupId());
        modelAndView.addObject("project", project);
        modelAndView.addObject("group", projectGroup);
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @GetMapping("/web/project/add")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView addProjectPage(HttpServletRequest request) throws Exception {
        ModelAndView modelAndView = new ModelAndView("admin/projectAdd");
        AppContext context = AppContext.getAppContext(request);
        modelAndView.addObject("currentRootPath", new File(SystemConsts.ROOT_STORAGE_PATH).getCanonicalPath());
        modelAndView.addObject("groupList", projectService.
                getProjectGroupUnderUser(context.getUserId(), context.getCurrentRole().getId()));
        modelAndView.addObject("stageList", MasterCodeUtils.getMasterCodeByType("project.stage.default"));
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @GetMapping("/web/projectGroup/add")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public ModelAndView addGroupPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/projectGroupAdd");
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @PostMapping("/web/project/add")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public AjaxResponseBody addProject(ProjectDto dto, HttpServletRequest request) throws Exception {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> violations = CommonValidator.validate(dto, null);
        if (!violations.isEmpty()) {
            responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            responseMgr.addValidations(violations);
        } else {
            projectService.saveNewProject(dto, AppContext.getAppContext(request));
        }
        return responseMgr.respond();
    }

    @GetMapping("/web/project/{id}/modify")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView prepareModify(@PathVariable("id") String id, HttpServletRequest request) {
        AppContext context = AppContext.getAppContext(request);
        projectService.verifyRequest(StringUtils.parseIfIsLong(id), context);
        ModelAndView modelAndView = new ModelAndView("admin/projectModify");
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), true);
        ProjectGroup projectGroup = projectService.getProjectGroupById(project.getGroupId());
        modelAndView.addObject("project", project);
        modelAndView.addObject("group", projectGroup);
        modelAndView.addObject("stageList", MasterCodeUtils.getMasterCodeByType("project.stage.default"));
        modelAndView.addObject("role", context.getRoleId());
        SessionManager.setAttribute(SESSION_KEY, project);
        return modelAndView;
    }

    @GetMapping("/web/project/selector")
    public AjaxResponseBody prepareProjectSelector() {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        responseMgr.addDataObjects(projectService.getProjectsByIsPublic(AppConsts.NO));
        return responseMgr.respond();
    }

    @GetMapping("/web/project/module/{id}")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public AjaxResponseBody retrieveForModify(@PathVariable("id") String id) {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Module module = projectService.getModuleById(StringUtils.parseIfIsLong(id));
        responseMgr.addDataObject(module);
        return responseMgr.respond();
    }

    @PostMapping("/web/project")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public AjaxResponseBody saveProject(ProjectDto dto) throws Exception {
        verifyRequest(dto.getProjectId());
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> violations = CommonValidator.validate(dto, null);
        if (!violations.isEmpty()) {
            responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            responseMgr.addValidations(violations);
        } else {
            projectService.updateProject(dto);
        }
        return responseMgr.respond();
    }

    @PostMapping("/web/project/module")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public AjaxResponseBody saveModule(@RequestBody Map<String, Object> params) {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        String id = (String) params.get("moduleId");
        String projectId = (String) params.get("projectId");
        String moduleName = (String) params.get("moduleName");
        String moduleDesc = (String) params.get("moduleDesc");
        String action = (String) params.get("action");
        if (!"delete".equals(action)) {
            Map<String, String> errors = new ModuleValidator().doValidate(params);
            if (!errors.isEmpty()) {
                responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
                responseMgr.addErrors(errors);
            } else {
                if (StringUtils.parseIfIsLong(id) != null && "modify".equals(action)) {
                    Module module = projectService.getModuleById(StringUtils.parseIfIsLong(id));
                    if (module != null) {
                        module.setName(moduleName);
                        module.setDescription(moduleDesc);
                        JPAExecutor.update(module);
                        responseMgr.addDataObjects(projectService.getModulesByProjectId(module.getProjectId()));
                    }
                } else if (StringUtils.parseIfIsLong(projectId) != null && "add".equals(action)) {
                    Project project = projectService.getProjectById(StringUtils.parseIfIsLong(projectId), false);
                    if (project != null) {
                        Module module = new Module();
                        module.setProjectId(project.getId());
                        module.setName(moduleName);
                        module.setDescription(moduleDesc);
                        JPAExecutor.save(module);
                        responseMgr.addDataObjects(projectService.getModulesByProjectId(module.getProjectId()));
                    }
                }
            }
        } else {
            Module module = projectService.getModuleById(StringUtils.parseIfIsLong(id));
            JPAExecutor.delete(module);
            responseMgr.addDataObjects(projectService.getModulesByProjectId(module.getProjectId()));
        }
        return responseMgr.respond();
    }

    @PostMapping("/web/project/list")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public AjaxResponseBody searchProject(@RequestBody SearchParam searchParam, HttpServletRequest request) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext appContext = AppContext.getAppContext(request);
        searchParam.addParam("userId", appContext.getUserId());
        searchParam.addParam("role", appContext.getCurrentRole().getId());
        SearchResult<ProjectVO> searchResult = projectService.search(searchParam);
        for (ProjectVO vo : searchResult.getResultList()) {
            vo.setRole(appContext.getRoleId());
        }
        manager.addDataObject(searchResult);
        return manager.respond();
    }

    @GetMapping("/web/project/{id}/retrieve")
    @ResponseBody
    public AjaxResponseBody retrieveProject(@PathVariable("id") String id) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), false);
        SessionManager.setAttribute(SESSION_KEY, project);
        manager.addDataObject(new ProjectVO(project));
        return manager.respond();
    }

    @PostMapping("/web/project/ban")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public AjaxResponseBody banProject(@RequestParam("id") String id) {
        verifyRequest(id);
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), false);
        projectService.banProject(project);
        return manager.respond();
    }

    @PostMapping("/web/project/activate")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public AjaxResponseBody activateProject(@RequestParam("id") String id) {
        verifyRequest(id);
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), false);
        projectService.activateProject(project);
        return manager.respond();
    }

    @GetMapping("/web/project/randomDir")
    @ResponseBody
    public AjaxResponseBody generateRandomDir() {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(projectService.generateRandomDir());
        return manager.respond();
    }

    @PostMapping("/web/projectGroup/list")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public AjaxResponseBody searchGroup(@RequestBody SearchParam searchParam, HttpServletRequest request) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext appContext = AppContext.getAppContext(request);
        searchParam.addParam("userId", appContext.getUserId());
        searchParam.addParam("role", appContext.getCurrentRole().getId());
        manager.addDataObject(projectService.searchGroup(searchParam));
        return manager.respond();
    }

    @PostMapping("/web/projectGroup/add")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public AjaxResponseBody addProjectGroup(ProjectGroup projectGroup) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> violations = CommonValidator.validate(projectGroup, null);
        if (!violations.isEmpty()) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(violations);
        } else {
            projectService.saveProjectGroup(projectGroup);
        }
        return manager.respond();
    }

    @GetMapping("/web/projectGroup/{id}/modify")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public ModelAndView prepareModifyGroup(@PathVariable("id") String id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/projectGroupModify");
        modelAndView.addObject("group", projectService.getProjectGroupById(StringUtils.parseIfIsLong(id)));
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @GetMapping("/web/projectGroup/{id}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public ModelAndView prepareDisplayGroup(@PathVariable("id") String id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/projectGroupDisplay");
        modelAndView.addObject("group", projectService.getProjectGroupById(StringUtils.parseIfIsLong(id)));
        modelAndView.addObject("projects", projectService.getProjectsUnderGroup(StringUtils.parseIfIsLong(id)));
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @PostMapping("/web/projectGroup")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public AjaxResponseBody saveProjectGroup(ProjectGroup projectGroup) throws Exception {
        return addProjectGroup(projectGroup);
    }

    private void verifyRequest(String projectId) {
        Project obj = (Project) SessionManager.getAttribute(SESSION_KEY);
        if (obj == null || !obj.getId().toString().equals(projectId)) {
            throw new PermissionDeniedException("请求参数与预期不一致");
        }
    }
}
