package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.dto.UserDto;
import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.admin.vo.UserAccountVO;
import org.legion.aegis.admin.vo.UserProjectVO;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.aop.permission.RequiresRoles;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class UserAccountController {

    private final ProjectService projectService;
    private final UserAccountService accountService;

    @Autowired
    public UserAccountController(ProjectService projectService, UserAccountService accountService) {
        this.projectService = projectService;
        this.accountService = accountService;
    }

    @GetMapping("/web/user/add")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public ModelAndView redirectAddUserPage(HttpServletRequest request) {
        AppContext context = AppContext.getAppContext(request);
        ModelAndView modelAndView = new ModelAndView("admin/userAdd");
/*        modelAndView.addObject("projects", projectService.
                getAllProjectsForSearchSelector(context.getUserId(), context.getRoleId()));
        modelAndView.addObject("groups", projectService.
                getProjectGroupUnderUser(context.getUserId(), context.getRoleId()));*/
        modelAndView.addObject("roles", accountService.getAllRoles());
        modelAndView.addObject("role", context.getRoleId());
        return modelAndView;
    }

    @GetMapping("/web/user")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView redirectUserPage(HttpServletRequest request) {
        AppContext context = AppContext.getAppContext(request);
        ModelAndView modelAndView = new ModelAndView("admin/userList");
        modelAndView.addObject("roles", accountService.getAllRolesForSearchSelector());
        modelAndView.addObject("role", context.getRoleId());
        modelAndView.addObject("projects", projectService.
                getAllProjectsForSearchSelector(context.getUserId(), context.getRoleId()));
        return modelAndView;
    }

    @GetMapping("/web/user/{id}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView display(@PathVariable("id") String id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/userDisplay");
        Long userId = StringUtils.parseIfIsLong(id);
        UserAccountVO userVO = accountService.searchUserInfo(userId);
        List<UserProjectVO> projectVOList = accountService.searchUserProjects(userId);
        modelAndView.addObject("user", userVO);
        modelAndView.addObject("projects", projectVOList);
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @PostMapping("/web/user/add")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    @ResponseBody
    public AjaxResponseBody addUser(@RequestBody UserDto userDto) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> violations = CommonValidator.validate(userDto, null);
        if (violations.isEmpty()) {
            UserAccount userAccount = BeanUtils.mapFromDto(userDto, UserAccount.class);
            userAccount.setDomain(AppConsts.USER_DOMAIN_INTRANET);
            userAccount.setDisplayName(userAccount.getLoginId());
            UserRole role = accountService.getRoleById(userDto.getRole());
            List<Project> projects = new ArrayList<>();
            for (String projectId : userDto.getProject()) {
                Project project = projectService.getProjectById(StringUtils.parseIfIsLong(projectId), false);
                projects.add(project);
            }
            UserAccount createdUser = accountService.createUser(userAccount, List.of(role), projects);
            accountService.sendAcknowledgementEmail(createdUser);
        } else {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(violations);
        }
        return manager.respond();
    }

    @PostMapping("/web/user/list")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    @ResponseBody
    public AjaxResponseBody search(@RequestBody SearchParam searchParam, HttpServletRequest request) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext appContext = AppContext.getAppContext(request);
        searchParam.addParam("userId", appContext.getUserId());
        searchParam.addParam("role", appContext.getCurrentRole().getId());
        manager.addDataObject(new SearchResult<>(accountService.searchUsers(searchParam), searchParam));
        return manager.respond();
    }

    @GetMapping("/web/user/{id}/modify")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView prepareModify(@PathVariable("id") String id, HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/userModify");
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @GetMapping("/web/user/selectProject/{roleId}")
    @ResponseBody
    public AjaxResponseBody retrieveProjectSelector(@PathVariable("roleId") String roleId) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext context = AppContext.getFromWebThread();
        if (AppConsts.ROLE_QA_SUPERVISOR.equals(roleId) || AppConsts.ROLE_DEV_SUPERVISOR.equals(roleId)) {
            List<ProjectGroup> groupList = projectService.getProjectGroupUnderUser(context.getUserId(), context.getRoleId());
            manager.addDataObjects(groupList);
        } else {
            List<Project> projectList = projectService.getAllProjects();
            manager.addDataObjects(projectList);
        }
        return manager.respond();
    }

}
