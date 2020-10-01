package org.legion.aegis.issuetracker.controller;

import org.legion.aegis.admin.service.ProjectService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.issuetracker.service.IssueStatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
public class IssueStatisticsController {

    private final ProjectService projectService;
    private final IssueStatisticsService statisticsService;

    public IssueStatisticsController(ProjectService projectService, IssueStatisticsService statisticsService) {
        this.projectService = projectService;
        this.statisticsService = statisticsService;
    }

    @GetMapping("/web/issue/statistics")
    @RequiresLogin
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView("issue/statistics");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        modelAndView.addObject("projects", projectService.getAccessibleProjectsForUser(context, null));
        return modelAndView;
    }

    @GetMapping("/web/issue/statistics/{action}")
    @RequiresLogin
    @ResponseBody
    public AjaxResponseBody retrieveStatusStat(@PathVariable("action") String action, HttpServletRequest request) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        if ("status".equals(action)) {
            String projectId = request.getParameter("projectId");
            manager.addDataObjects(statisticsService.prepareStatusStatData(StringUtils.parseIfIsLong(projectId)));
        } else if ("res".equals(action)) {
            String projectId = request.getParameter("projectId");
            manager.addDataObjects(statisticsService.prepareResStatData(StringUtils.parseIfIsLong(projectId)));
        } else if ("user".equals(action)) {
            String projectId = request.getParameter("projectId");
            manager.addDataObjects(statisticsService.prepareUserStat(StringUtils.parseIfIsLong(projectId)));
        }
        return manager.respond();
    }

    @GetMapping("/web/issue/statistics/view/{projectId}/{id}")
    @RequiresLogin
    public ModelAndView display(@PathVariable("id") String userId, @PathVariable("projectId") String projectId ) {
        ModelAndView modelAndView = new ModelAndView("issue/userStatDisplay");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        modelAndView.addObject("stat", statisticsService.displayUserStat(StringUtils.parseIfIsLong(userId),
                StringUtils.parseIfIsLong(projectId)));
        return modelAndView;
    }
}
