package org.legion.aegis.admin.controller;

import org.apache.commons.compress.utils.Lists;
import org.legion.aegis.admin.dto.UserDto;
import org.legion.aegis.admin.entity.*;
import org.legion.aegis.admin.generator.NoticeOfNewUserGenerator;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.admin.validator.ResetPasswordValidator;
import org.legion.aegis.admin.validator.UserModifyValidator;
import org.legion.aegis.admin.vo.ProjectVO;
import org.legion.aegis.admin.vo.UserAccountVO;
import org.legion.aegis.admin.vo.UserProjectVO;
import org.legion.aegis.admin.vo.UserSearchVO;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.aop.permission.RequiresRoles;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.legion.aegis.common.webmvc.NetworkFileTransfer;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

@Controller
public class UserAccountController {

    private final ProjectService projectService;
    private final UserAccountService accountService;
    private static final String SESSION_KEY  = "SESSION_USER";
    public static final String PROJECT_KEY  = "AUTH_PROJECT";
    public static final String GROUP_KEY    = "AUTH_GROUP";

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
        modelAndView.addObject("roles", accountService.getRoleSelectorForSearch(context));
        modelAndView.addObject("role", context.getRoleId());
        modelAndView.addObject("projects", projectService.
                getAllProjectsForSearchSelector(context.getUserId(), context.getRoleId()));
        return modelAndView;
    }

    @GetMapping("/web/user/{id}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView display(@PathVariable("id") String id, HttpServletRequest request) {
        accountService.verifyRequest(StringUtils.parseIfIsLong(id));
        ModelAndView modelAndView = new ModelAndView("admin/userDisplay");
        Long userId = StringUtils.parseIfIsLong(id);
        UserAccountVO userVO = accountService.searchUserInfo(userId);
        List<UserProjectVO> projectVOList = accountService.searchUserProjects(userId, userVO.getRoleId());
        modelAndView.addObject("user", userVO);
        modelAndView.addObject("projects", projectVOList);
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        return modelAndView;
    }

    @PostMapping("/web/user/add")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    @ResponseBody
    public AjaxResponseBody createUser(@RequestBody UserDto userDto) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> violations = CommonValidator.validate(userDto, null);
        if (violations.isEmpty()) {
            UserAccount userAccount = BeanUtils.mapFromDto(userDto, UserAccount.class);
            userAccount.setDomain(AppConsts.USER_DOMAIN_INTRANET);
            userAccount.setDisplayName(userAccount.getLoginId());
            UserRole role = accountService.getRoleById(userDto.getRole());
            UserAccount createdUser = accountService.createUser(userAccount, List.of(role), userDto.getProject());
            accountService.sendAcknowledgementEmail(createdUser);
            SessionManager.setAttribute("notice", new NoticeOfNewUserGenerator(createdUser).generate());
            manager.addDataObject(createdUser.getId());
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
        SessionManager.removeAttribute(SESSION_KEY);
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext appContext = AppContext.getAppContext(request);
        searchParam.addParam("userId", appContext.getUserId());
        searchParam.addParam("role", appContext.getCurrentRole().getId());
        manager.addDataObject(accountService.searchUsers(searchParam));
        return manager.respond();
    }

    @GetMapping("/web/user/{id}/modify")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    public ModelAndView prepareModify(@PathVariable("id") String id, HttpServletRequest request) {
        AppContext context = AppContext.getAppContext(request);
        accountService.verifyRequest(StringUtils.parseIfIsLong(id));
        UserAccountVO userVO = accountService.searchUserInfo(StringUtils.parseIfIsLong(id));
        ModelAndView modelAndView = new ModelAndView("admin/userModify");
        List<UserProjectVO> projectVOList = accountService.searchUserProjects(StringUtils.parseIfIsLong(id), userVO.getRoleId());
        List<UserProjectVO> publicList = new ArrayList<>();
        List<UserProjectVO> privateList = new ArrayList<>();
        List<Long> validProjectId = new ArrayList<>();
        List<Long> validGroupId = new ArrayList<>();
        SessionManager.setAttribute(PROJECT_KEY, validProjectId);
        SessionManager.setAttribute(GROUP_KEY, validGroupId);
        if (UserAccountService.isSupervisor(context.getRoleId())) {
            List<ProjectGroup> supervisorGroupList = projectService.getProjectGroupUnderUser(context.getUserId(), context.getRoleId());
            for (UserProjectVO vo : projectVOList) {
                for (ProjectGroup group : supervisorGroupList) {
                    if (vo.getGroupId().equals(group.getId())) {
                        vo.setHasPermission(true);
                        validProjectId.add(vo.getProjectId());
                    }
                }
            }
        }
        //for group table
        if (!AppConsts.ROLE_SYSTEM_ADMIN.equals(userVO.getRoleId())) {
            List<UserProjectVO> groupVOList = new ArrayList<>();
            List<ProjectGroup> groups = projectService.getProjectGroupUnderUser(userVO.getId(), userVO.getRoleId());
            List<ProjectGroup> supervisorGroupList = projectService.getProjectGroupUnderUser(context.getUserId(), context.getRoleId());
            for (ProjectGroup group : groups) {
                for (UserProjectVO vo : projectVOList) {
                    if (group.getId().equals(vo.getGroupId())) {
                        groupVOList.add(vo);
                        break;
                    }
                }
            }
            for (UserProjectVO vo : groupVOList) {
                for (ProjectGroup group : supervisorGroupList) {
                    if (vo.getGroupId().equals(group.getId())) {
                        vo.setHasPermission(true);
                        validGroupId.add(vo.getGroupId());
                    }
                }
            }
            modelAndView.addObject("groups", groupVOList);
        }
        if (UserAccountService.isDevQA(userVO.getRoleId())) {
            modelAndView.addObject("selector", accountService.prepareProjectSelectorForModify(userVO.getId()));
        } else {
            modelAndView.addObject("selector", accountService.prepareGroupSelectorForModify(userVO.getId()));
        }
        for (UserProjectVO vo : projectVOList) {
            if (AppConsts.YES.equals(vo.getIsPublic())) {
                publicList.add(vo);
            } else {
                privateList.add(vo);
            }
        }
        modelAndView.addObject("projects", privateList);
        modelAndView.addObject("public", publicList);
        modelAndView.addObject("role", AppContext.getAppContext(request).getRoleId());
        modelAndView.addObject("user", userVO);
        SessionManager.setAttribute(SESSION_KEY, userVO);
        return modelAndView;
    }

    @GetMapping("/web/user/selectProject/{roleId}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    @ResponseBody
    public AjaxResponseBody retrieveProjectSelector(@PathVariable("roleId") String roleId) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext context = AppContext.getFromWebThread();
        if (AppConsts.ROLE_QA_SUPERVISOR.equals(roleId) || AppConsts.ROLE_DEV_SUPERVISOR.equals(roleId)) {
            List<ProjectGroup> groupList = projectService.getProjectGroupUnderUser(context.getUserId(), context.getRoleId());
            manager.addDataObjects(groupList);
        } else {
            manager.addDataObjects(projectService.getProjectSelector());
        }
        return manager.respond();
    }

    @GetMapping("/web/user/add/{id}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public ModelAndView userAddAcknowledge(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("admin/userAddAcknowledge");
        Long userId = StringUtils.parseIfIsLong(id);
        UserAccountVO userVO = accountService.searchUserInfo(userId);
        List<UserProjectVO> projectVOList = accountService.searchUserProjects(userId, userVO.getRoleId());
        modelAndView.addObject("role", AppContext.getFromWebThread().getRoleId());
        modelAndView.addObject("user", userVO);
        modelAndView.addObject("projects", projectVOList);
        return modelAndView;
    }

    @GetMapping("/web/user/add/download")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public void download(HttpServletResponse response) throws Exception {
        NetworkFileTransfer.download((byte[]) SessionManager.getAttribute("notice"), "账号创建通知.pdf", response);
    }

    @GetMapping("/web/user/{id}/modify/{projectOrGroupId}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    @ResponseBody
    public AjaxResponseBody prepareDelete(@PathVariable("id")String userId,
                                          @PathVariable("projectOrGroupId") String projectOrGroupId, HttpServletRequest request) {
        verifyRequest(userId);
        String type = request.getParameter("type");
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        if ("project".equals(type)) {
            manager.addDataObject(projectService.getProjectById(StringUtils.parseIfIsLong(projectOrGroupId), false));
        } else if ("group".equals(type)){
            manager.addDataObject(projectService.getProjectGroupById(StringUtils.parseIfIsLong(projectOrGroupId)));
        }
        return manager.respond();
    }

    @PostMapping("/web/user/{id}/modify/{action}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN, AppConsts.ROLE_DEV_SUPERVISOR})
    @ResponseBody
    @SuppressWarnings("unchecked")
    public AjaxResponseBody submitAction(@PathVariable("id") String userId, @PathVariable("action") String action, @RequestBody Map<String, Object> params) {
        verifyRequest(userId);
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        if ("removeProject".equals(action)) {
            String projectId = (String) params.get("projectId");
            accountService.removeProjectAssignment(StringUtils.parseIfIsLong(userId), StringUtils.parseIfIsLong(projectId));
        } else if ("removeGroup".equals(action)) {
            String groupId = (String) params.get("groupId");
            accountService.removeGroup(StringUtils.parseIfIsLong(userId), StringUtils.parseIfIsLong(groupId));
        } else if ("save".equals(action)) {
            Map<String, String> errors = new UserModifyValidator().doValidate(params);
            if (!errors.isEmpty()) {
                manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
                manager.addErrors(errors);
            } else {
                String name = (String) params.get("name");
                String email = (String) params.get("email");
                String deactivateAt = (String) params.get("deactivateAt");
                String[] newAddProjects =  ((List<String>)params.get("projectSelector")).toArray(new String[0]);
                UserDto userDto = new UserDto();
                userDto.setUserId(userId);
                userDto.setName(name);
                userDto.setEmail(email);
                userDto.setDeactivatedAt(DateUtils.parseDatetime(deactivateAt));
                userDto.setProject(newAddProjects);
                accountService.updateUser(userDto);
            }
        }
        return manager.respond();
    }

    @PostMapping("/web/user/resetPassword")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    @ResponseBody
    public AjaxResponseBody resetPassword(@RequestBody Map<String, Object> params) {
        UserAccountVO userAccountVO = (UserAccountVO) SessionManager.getAttribute(SESSION_KEY);
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        if (userAccountVO != null) {
            Map<String, String> errors = new ResetPasswordValidator().doValidate(params);
            if (!errors.isEmpty()) {
                manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
                manager.addErrors(errors);
            } else {
                UserAccount userAccount = accountService.getUserById(userAccountVO.getId());
                accountService.resetPassord(userAccount, (String) params.get("pwd1"));
            }
        }
        return manager.respond();
    }

    private void verifyRequest(String userId) {
        UserAccountVO userAccountVO = (UserAccountVO) SessionManager.getAttribute(SESSION_KEY);
        if (userAccountVO == null || !userAccountVO.getId().equals(StringUtils.parseIfIsLong(userId))) {
            throw new PermissionDeniedException("请求参数与预期不一致");
        }
    }
}
