package org.legion.aegis.issuetracker.controller;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.issuetracker.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class IssueController {

    private final IssueService issueService;

    @Autowired
    public IssueController(IssueService issueService) {
        this.issueService = issueService;
    }

    @PostMapping("/web/issue/view")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody search(@RequestBody SearchParam param) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        AppContext context = AppContext.getFromWebThread();
        param.addParam("roleId", context.getRoleId());
        param.addParam("userId", context.getUserId());
        manager.addDataObject(issueService.search(param));
        return manager.respond();
    }

    @GetMapping("/web/issue/view")
    @RequiresLogin
    public ModelAndView main() {
        ModelAndView modelAndView = new ModelAndView("issue/dashboard");
        modelAndView.addObject("role", AppContext.getFromWebThread().getRoleId());
        return modelAndView;
    }
}
