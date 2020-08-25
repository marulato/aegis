package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.consts.AppConsts;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
public class ProjectMgrController {

    @GetMapping("/web/project")
    public String getProjectDisplayPage(HttpServletRequest request) {
        return "admin/projectList";
    }

    @GetMapping("/web/project/display")
    public String display() {
        return "admin/projectDisplay";
    }

    @GetMapping("/web/project/add")
    public String add() {
        return "admin/projectAdd";
    }

    @GetMapping("/web/project/modify")
    public String addSub() {
        return "admin/projectModify";
    }

    @PostMapping("/web/project/list")
    @ResponseBody
    public AjaxResponseBody getProjects(@RequestParam Map<String, String> param) {
        System.out.println(param);
        AjaxResponseManager manager = AjaxResponseManager.create(200);
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Project project = new Project();
            project.setName("test" + i);
            projects.add(project);
        }
        List<Project> result = new ArrayList<>();
        int page = Integer.parseInt(param.get("page")) - 1;
        int pageSize = Integer.parseInt(param.get("pageSize"));
        for (int i = page * pageSize; i < page * pageSize + pageSize; i++) {
            result.add(projects.get(i));
        }
        manager.addDataObjects(result);
        return manager.respond();
    }
}
