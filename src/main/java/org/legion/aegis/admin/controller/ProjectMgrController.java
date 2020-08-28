package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.dto.ProjectDto;
import org.legion.aegis.admin.entity.Project;
import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.base.SearchResult;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.exception.RecordsNotFoundException;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.MiscGenerator;
import org.legion.aegis.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Controller
public class ProjectMgrController {

    private final ProjectService projectService;

    @Autowired
    public ProjectMgrController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @GetMapping("/web/project")
    public String getProjectDisplayPage(HttpServletRequest request) {
        return "admin/projectList";
    }

    @GetMapping("/web/project/{id}")
    public ModelAndView display(@PathVariable("id") String id) throws Exception{
        ModelAndView modelAndView = new ModelAndView("admin/projectDisplay");
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), true);
        //ProjectDto dto = BeanUtils.mapFromPO(project, ProjectDto.class, null);
        if (project == null) {
            throw new RecordsNotFoundException();
        }
        modelAndView.addObject("project", project);
        return modelAndView;
    }

    @GetMapping("/web/project/acknowledge")
    public String acknowledge() {
        return "admin/projectAcknowledge";
    }

    @GetMapping("/web/project/add")
    public String add() {
        return "admin/projectAdd";
    }

    @GetMapping("/web/project/modify/{id}")
    public ModelAndView prepareModify(@PathVariable("id") String id) {
        ModelAndView modelAndView = new ModelAndView("admin/projectModify");
        Project project = projectService.getProjectById(StringUtils.parseIfIsLong(id), true);
        if (project == null) {
            throw new RecordsNotFoundException();
        }
        modelAndView.addObject("project", project);
        return modelAndView;
    }

    @GetMapping("/web/project/get")
    public AjaxResponseBody prepareProjectSelector() {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        responseMgr.addDataObjects(projectService.getAllProjects());
        return responseMgr.respond();
    }

    @PostMapping("/web/project/list")
    @ResponseBody
    public AjaxResponseBody prepareTable(@RequestBody SearchParam searchParam) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(200);
        List<Project> projectList = projectService.search(searchParam);
        List<ProjectDto> dtoList = new ArrayList<>();
        for (Project project : projectList) {
            ProjectDto dto = BeanUtils.mapFromPO(project, ProjectDto.class, "yyyy/MM/dd");
            if (dto != null) {
                if (AppConsts.STATUS_ACTIVE.equals(project.getStatus())) {
                    dto.setStatus(AppConsts.STATUS_ACTIVE_CHN);
                } else if (AppConsts.STATUS_EXPIRED.equals(project.getStatus())) {
                    dto.setStatus(AppConsts.STATUS_EXPIRED_CHN);
                }
                dtoList.add(dto);
            }
        }
        SearchResult<ProjectDto> searchResult = new SearchResult<>();
        searchResult.setDraw(searchParam.getDraw());
        searchResult.setResultList(dtoList);
        manager.addDataObject(searchResult);
        return manager.respond();
    }
}
