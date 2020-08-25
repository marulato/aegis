package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.MiscGenerator;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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
    public AjaxResponseBody getProjects(@RequestBody SearchParam searchParam) {
        System.out.println(searchParam.getParams());
        AjaxResponseManager manager = AjaxResponseManager.create(200);
        SearchResult<Project> searchResult = new SearchResult<>();
        List<Project> projects = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Project project = new Project();
            project.setDescription("test" + i);
            project.setName(MiscGenerator.generateInitialPassword(10));
            projects.add(project);
        }
        List<Project> result = new ArrayList<>();
        int page = searchParam.getPageNo() - 1;
        int pageSize = searchParam.getPageSize();
        for (int i = page * pageSize; i < page * pageSize + pageSize; i++) {
            result.add(projects.get(i));
        }
        if (searchParam.getOrderColumnNo() == 1) {
            result.sort(Comparator.comparing(Project::getName));
        }
        searchResult.setResultList(result);
        searchResult.setTotalCounts(projects.size());
        manager.addDataObject(searchResult);
        return manager.respond();
    }
}
