package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.dto.ProjectDto;
import org.legion.aegis.admin.dto.ProjectGroupDto;
import org.legion.aegis.admin.entity.Module;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.ProjectGroup;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.validator.ModuleValidator;
import org.legion.aegis.admin.vo.ProjectGroupVO;
import org.legion.aegis.admin.vo.ProjectVO;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.consts.SystemConsts;
import org.legion.aegis.common.exception.RecordsNotFoundException;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
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

    @Autowired
    public ProjectMgrController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/web/project")
    public String getProjectPage() {
        return "admin/projectList";
    }

    @GetMapping("/web/projectGroup")
    public String getProjectGroupPage() {
        return "admin/projectGroupList";
    }

    @GetMapping("/web/project/{id}")
    public ModelAndView display(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("admin/projectDisplay");
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), true);
        //ProjectDto dto = BeanUtils.mapFromPO(project, ProjectDto.class, null);
        if (project == null) {
            throw new RecordsNotFoundException();
        }
        modelAndView.addObject("project", project);
        return modelAndView;
    }

    @GetMapping("/web/project/add")
    public ModelAndView addProjectPage(HttpServletRequest request) throws Exception {
        ModelAndView modelAndView = new ModelAndView("admin/projectAdd");
        AppContext context = AppContext.getAppContext(request);
        modelAndView.addObject("currentRootPath", new File(SystemConsts.ROOT_STORAGE_PATH).getCanonicalPath());
        modelAndView.addObject("groupList", projectService.
                getProjectGroupUnderUser(context.getUserId(), context.getCurrentRole().getId()));
        modelAndView.addObject("stageList", MasterCodeUtils.getMasterCodeByType("project.stage.default"));
        return modelAndView;
    }

    @GetMapping("/web/projectGroup/add")
    public String addGroupPage() {
        return "admin/projectGroupAdd";
    }

    @PostMapping("/web/project/add")
    @ResponseBody
    public AjaxResponseBody addProject(ProjectDto dto, HttpServletRequest request) throws Exception {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Map<String, List<String>> errors = CommonValidator.doValidation(dto, null);
        if (!errors.isEmpty()) {
            responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            responseMgr.addValidations(errors);
        } else {
            projectService.saveNewProject(dto, AppContext.getAppContext(request));
        }
        return responseMgr.respond();
    }

    @GetMapping("/web/project/{id}/modify")
    public ModelAndView prepareModify(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("admin/projectModify");
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), true);
        if (project == null) {
            throw new RecordsNotFoundException();
        }
        modelAndView.addObject("project", project);
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
    public AjaxResponseBody retrieveForModify(@PathVariable("id") String id) {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Module module = projectService.getModuleById(StringUtils.parseIfIsLong(id));
        responseMgr.addDataObject(module);
        return responseMgr.respond();
    }

    @PostMapping("/web/project")
    @ResponseBody
    public AjaxResponseBody saveProject(ProjectDto dto) throws Exception {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Map<String, List<String>> errors = CommonValidator.doValidation(dto, null);
        if (!errors.isEmpty()) {
            responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            responseMgr.addValidations(errors);
        } else {
            projectService.updateProject(dto);
        }
        return responseMgr.respond();
    }

    @PostMapping("/web/project/module")
    @ResponseBody
    public AjaxResponseBody saveModule(@RequestBody Map<String, String> params) {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        String id = params.get("moduleId");
        String projectId = params.get("projectId");
        String moduleName = params.get("moduleName");
        String moduleDesc = params.get("moduleDesc");
        String action = params.get("action");
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
    public AjaxResponseBody search(@RequestBody SearchParam searchParam, HttpServletRequest request) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext appContext = AppContext.getAppContext(request);
        searchParam.addParam("userId", appContext.getUserId());
        searchParam.addParam("role", appContext.getCurrentRole().getId());
        List<ProjectVO> projectList = projectService.search(searchParam);
        //List<ProjectVO> dtoList = projectService.toProjectView(projectList);
        manager.addDataObject(new SearchResult<>(projectList, searchParam));
        return manager.respond();
    }

    @GetMapping("/web/project/{id}/retrieve")
    @ResponseBody
    public AjaxResponseBody retrieveProject(@PathVariable("id") String id) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), false);
        manager.addDataObject(projectService.toProjectView(project));
        return manager.respond();
    }

    @PostMapping("/web/project/{id}/ban")
    @ResponseBody
    public AjaxResponseBody banProject(@PathVariable("id") String id) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), false);
        projectService.banProject(project);
        return manager.respond();
    }

    @PostMapping("/web/project/{id}/activate")
    @ResponseBody
    public AjaxResponseBody activateProject(@PathVariable("id") String id) {
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
    public AjaxResponseBody searchGroup(@RequestBody SearchParam searchParam, HttpServletRequest request) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext appContext = AppContext.getAppContext(request);
        searchParam.addParam("userId", appContext.getUserId());
        searchParam.addParam("role", appContext.getCurrentRole().getId());
        List<ProjectGroup> projectGroupList = projectService.searchGroup(searchParam);
        List<ProjectGroupVO> dtoList = new ArrayList<>();
        for (ProjectGroup group : projectGroupList) {
            dtoList.add(new ProjectGroupVO(group));
        }
        manager.addDataObject(new SearchResult<>(dtoList, searchParam));
        return manager.respond();
    }

    @PostMapping("/web/projectGroup/add")
    @ResponseBody
    public AjaxResponseBody addProjectGroup(ProjectGroup projectGroup) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Map<String, List<String>> errors = CommonValidator.doValidation(projectGroup, null);
        if (!errors.isEmpty()) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(errors);
        } else {
            projectService.saveProjectGroup(projectGroup);
        }
        return manager.respond();
    }

    @GetMapping("/web/projectGroup/{id}/modify")
    public ModelAndView prepareModifyGroup(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("admin/projectGroupModify");
        modelAndView.addObject("group", projectService.getProjectGroupById(StringUtils.parseIfIsLong(id)));
        return modelAndView;
    }

    @GetMapping("/web/projectGroup/{id}")
    public ModelAndView prepareDisplayGroup(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("admin/projectGroupDisplay");
        modelAndView.addObject("group", projectService.getProjectGroupById(StringUtils.parseIfIsLong(id)));
        modelAndView.addObject("projects", projectService.getProjectsUnderGroup(StringUtils.parseIfIsLong(id)));
        return modelAndView;
    }

    @PostMapping("/web/projectGroup")
    @ResponseBody
    public AjaxResponseBody saveProjectGroup(ProjectGroup projectGroup) throws Exception {
        return addProjectGroup(projectGroup);
    }
}
