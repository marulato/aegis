package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.dto.UserDto;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.entity.UserRole;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

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
    public ModelAndView redirectAddUserPage() {
        ModelAndView modelAndView = new ModelAndView("admin/userAdd");
        modelAndView.addObject("projects", projectService.getAllProjects());
        modelAndView.addObject("roles", accountService.getAllRoles());
        return modelAndView;
    }

    @GetMapping("/web/user")
    public String redirectUserPage() {
        return "admin/userList";
    }

    @GetMapping("/web/user/display")
    public String display() {
        return "admin/userDisplay";
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
                Project project = projectService.getProjectById(StringUtils.parseIfIsLong(projectId));
                projects.add(project);
            }
            UserAccount createdUser = accountService.createUser(userAccount, List.of(role), projects);
            accountService.sendAcknowledgementEmail(createdUser);
        }
    }

}
