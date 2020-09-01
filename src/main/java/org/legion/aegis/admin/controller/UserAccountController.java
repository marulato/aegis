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
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView redirectAddUserPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/userAdd");
        modelAndView.addObject("projects", projectService.
                getAllProjectsForSearchSelector(AppContext.getAppContext(request).getUserId()));
        modelAndView.addObject("roles", accountService.getAllRoles());
        return modelAndView;
    }

    @GetMapping("/web/user")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView redirectUserPage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("admin/userList");
        modelAndView.addObject("roles", accountService.getAllRolesForSearchSelector());
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        modelAndView.addObject("projects", projectService.
                getAllProjectsForSearchSelector(AppContext.getAppContext(request).getUserId()));
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
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
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

}
