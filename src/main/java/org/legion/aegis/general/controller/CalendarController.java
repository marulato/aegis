package org.legion.aegis.general.controller;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CalendarController {

    @GetMapping("/web/calendar")
    @RequiresLogin
    public ModelAndView inboxPage() {
        ModelAndView modelAndView = new ModelAndView("general/calendar");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        return modelAndView;
    }
}
