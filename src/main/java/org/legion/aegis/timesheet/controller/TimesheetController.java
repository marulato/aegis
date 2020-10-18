package org.legion.aegis.timesheet.controller;

import org.legion.aegis.admin.entity.MasterCode;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.CollectionUtils;
import org.legion.aegis.common.utils.MasterCodeUtils;
import org.legion.aegis.common.utils.SpringUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.legion.aegis.timesheet.dao.TimesheetDAO;
import org.legion.aegis.timesheet.dto.EventDto;
import org.legion.aegis.timesheet.entity.Timesheet;
import org.legion.aegis.timesheet.service.TimesheetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
public class TimesheetController {

    private final TimesheetService timesheetService;

    @Autowired
    public TimesheetController(TimesheetService timesheetService) {
        this.timesheetService = timesheetService;
    }

    @GetMapping("/web/calendar")
    @RequiresLogin
    public ModelAndView inboxPage() {
        ModelAndView modelAndView = new ModelAndView("general/calendar");
        AppContext context = AppContext.getFromWebThread();
        modelAndView.addObject("role", context.getRoleId());
        List<MasterCode> masterCodes = MasterCodeUtils.getMasterCodeByType("timesheet.event.type");
/*        List<MasterCode> types = CollectionUtils.getModifiableList(masterCodes, true);
        if (!AppConsts.ROLE_SYSTEM_ADMIN.equals(context.getRoleId())) {
            types.removeIf(var -> AppConsts.TIMESHEET_EVENT_PUBLIC.equals(var.getCode()));
        }*/
        modelAndView.addObject("types", masterCodes);
        return modelAndView;
    }

    @PostMapping("/web/calendar/event")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody saveCommonEvent(EventDto dto) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Long timesheetId = StringUtils.parseIfIsLong(dto.getTimesheetId());
        String profile;
        if (timesheetId == null || timesheetId <= 0) {
            profile = "create";
        } else {
            profile = "update";
        }
        List<ConstraintViolation> violations = CommonValidator.validate(dto, profile);
        if (!violations.isEmpty()) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(violations);
        } else {
            timesheetService.saveCommonEvent(dto);
        }
        return manager.respond();
    }

    @PostMapping("/web/calendar/searchEvents")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody searchEvents(@RequestBody Map<String, String> params) throws Exception {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        params.put("userId", String.valueOf(AppContext.getFromWebThread().getUserId()));
        manager.addDataObjects(timesheetService.searchEvents(params));
        return manager.respond();
    }

    @GetMapping("/web/calendar/{id}")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody displayEvent(@PathVariable("id") String timesheetId) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        manager.addDataObject(timesheetService.getCommonEvent(StringUtils.parseIfIsLong(timesheetId)));
        return manager.respond();
    }

    @GetMapping("/web/calendar/delete/{id}")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody deleteTimesheet(@PathVariable("id") String timesheetId) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        TimesheetDAO dao = SpringUtils.getBean(TimesheetDAO.class);
        Timesheet timesheet = dao.retrieveTimesheetById(StringUtils.parseIfIsLong(timesheetId));
        AppContext context = AppContext.getFromWebThread();
        if (timesheet != null && timesheet.getUserAcctId().equals(context.getUserId())) {
            timesheetService.deleteTimesheet(StringUtils.parseIfIsLong(timesheetId));
        } else {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
        }
        return manager.respond();
    }
}
