package org.legion.aegis.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectMgrController {

    @GetMapping("/web/project")
    public String getProjectDisplayPage() {
        return "admin/projectEnqiury";
    }

    @GetMapping("/web/project/display")
    public String display() {
        return "admin/projectDisplay";
    }

    @GetMapping("/web/project/add")
    public String add() {
        return "admin/projectAdd";
    }

    @GetMapping("/web/project/addSub")
    public String addSub() {
        return "admin/subProjectAdd";
    }
}
