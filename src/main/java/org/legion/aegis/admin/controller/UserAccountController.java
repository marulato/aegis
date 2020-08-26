package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.dto.UserDto;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class UserAccountController {

    private final ProjectService projectService;

    @Autowired
    public UserAccountController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/web/user/add")
    public ModelAndView redirectAddUserPage() {
        ModelAndView modelAndView = new ModelAndView("admin/userAdd");
        modelAndView.addObject("projects", projectService.getAllProjects());
        return modelAndView;
    }

    @GetMapping("/web/user/")
    public String redirectUserPage() {
        return "admin/userList";
    }

    @GetMapping("/web/user/display")
    public String display() {
        return "admin/userDisplay";
    }

    @PostMapping("/web/user/add/submit")
    public void addUser(UserDto userDto) throws Exception {
        CommonValidator.doValidation(userDto, null);

    }
}
