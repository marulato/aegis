package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.dto.UserDto;
import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
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
    public ModelAndView redirectAddUserPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/userAdd");
        modelAndView.addObject("projects", projectService.
                getAllProjectsForSearchSelector(AppContext.getAppContext(request).getUserId()));
        modelAndView.addObject("roles", accountService.getAllRoles());
        return modelAndView;
    }

    @GetMapping("/web/user")
    public ModelAndView redirectUserPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/userList");
        modelAndView.addObject("roles", accountService.getAllRolesForSearchSelector());
        modelAndView.addObject("projects", projectService.
                getAllProjectsForSearchSelector(AppContext.getAppContext(request).getUserId()));
        return modelAndView;
    }

    @GetMapping("/web/user/{id}")
    public ModelAndView display(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("admin/userDisplay");
        UserAccount userAccount = accountService.getUserById(StringUtils.parseIfIsLong(id));
        List<UserProjectAssign> projectAssignList = accountService.getUserProjectAssignments(StringUtils.parseIfIsLong(id));
        List<UserRoleAssign> roleAssignList = accountService.getRoleAssigments(StringUtils.parseIfIsLong(id));
        List<UserRole> roleList = new ArrayList<>();
        List<Project> projectList = new ArrayList<>();
        for (UserRoleAssign roleAssign : roleAssignList) {
            UserRole role = accountService.getRoleById(roleAssign.getRoleId());
            roleList.add(role);
        }
        for (UserProjectAssign projectAssign : projectAssignList) {
            projectList.add(projectService.getProjectById(projectAssign.getProjectId(), false));
        }
        modelAndView.addObject("user", userAccount);
        modelAndView.addObject("roles", roleList);
        modelAndView.addObject("projects", projectList);
        return modelAndView;
    }

    @PostMapping("/web/user/add")
    @ResponseBody
    public void addUser(@RequestBody UserDto userDto) throws Exception {
        Map<String, List<String>> errors = CommonValidator.doValidation(userDto, null);
        if (errors.isEmpty()) {
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
        }
    }

    @PostMapping("/web/user/list")
    @ResponseBody
    public AjaxResponseBody search(@RequestBody SearchParam searchParam) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(new SearchResult<>(accountService.searchUsers(searchParam), searchParam));
        return manager.respond();
    }

}
