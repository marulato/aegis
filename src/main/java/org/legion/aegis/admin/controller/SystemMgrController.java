package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.aop.permission.RequiresRoles;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.consts.AppConsts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class SystemMgrController {

    private final SystemMgrService systemMgrService;

    @Autowired
    public SystemMgrController(SystemMgrService systemMgrService) {
        this.systemMgrService = systemMgrService;
    }

    @GetMapping("/web/systemManagement/issueStatus")
    public ModelAndView mainPage() {
        ModelAndView modelAndView = new ModelAndView("admin/issueStatusList");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        return modelAndView;
    }

    @PostMapping("/web/systemManagement/issueStatus/list")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public AjaxResponseBody searchIssueStatus(@RequestBody SearchParam param) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(systemMgrService.searchIssueStatus(param));
        return manager.respond();
    }
}
