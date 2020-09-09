package org.legion.aegis.admin.controller;

import org.dom4j.DocumentException;
import org.legion.aegis.admin.dto.IssueStatusDto;
import org.legion.aegis.admin.entity.IssueStatus;
import org.legion.aegis.admin.service.SystemMgrService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.aop.permission.RequiresRoles;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.base.SearchParam;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.exception.InvalidXMLFormatException;
import org.legion.aegis.common.utils.BeanUtils;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.legion.aegis.common.webmvc.NetworkFileTransfer;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class SystemMgrController {

    private final SystemMgrService systemMgrService;
    private static final String SESSION_KEY_ISS = "SESSION_ISSUE_STATUS";

    @Autowired
    public SystemMgrController(SystemMgrService systemMgrService) {
        this.systemMgrService = systemMgrService;
    }

    @GetMapping("/web/systemManagement/issueStatus")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
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

    @GetMapping("/web/systemManagement/issueStatus/{statusCode}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public ModelAndView prepareModify(@PathVariable("statusCode") String statusCode) {
        ModelAndView modelAndView = new ModelAndView("admin/issueStatusModify");
        AppContext context = AppContext.getFromWebThread();
        IssueStatus issueStatus = systemMgrService.getIssueStatusByCode(statusCode);
        modelAndView.addObject("role", context.getRoleId());
        modelAndView.addObject("issueStatus", issueStatus);
        SessionManager.setAttribute(SESSION_KEY_ISS, issueStatus);
        return modelAndView;
    }

    @PostMapping("/web/systemManagement/issueStatus/{statusCode}")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    @ResponseBody
    public AjaxResponseBody doModify(@PathVariable("statusCode") String statusCode, IssueStatusDto dto) throws Exception {
        verifyRequestIssueStatus(statusCode);
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        List<ConstraintViolation> errors = CommonValidator.validate(dto, "modify");
        if (!errors.isEmpty()) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addValidations(errors);
        } else {
            IssueStatus issueStatus = BeanUtils.mapFromDto(dto, IssueStatus.class);
            systemMgrService.updateIssueStatus(issueStatus);
        }
        return manager.respond();
    }

    @GetMapping("/web/systemManagement/issueStatus/export")
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public void exportIssueStatus(HttpServletResponse response) throws Exception {
        NetworkFileTransfer.download(systemMgrService.exportIssueStatus(), "IssueStatus-" +
                DateUtils.getDateString(new Date(), "yyyy-MM-dd") + ".xml", response);
    }

    @PostMapping("/web/systemManagement/issueStatus/import")
    @ResponseBody
    @RequiresRoles({AppConsts.ROLE_SYSTEM_ADMIN})
    public AjaxResponseBody importIssueStatus(@RequestParam("uploadedFile") MultipartFile file) {
        AjaxResponseManager manager = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        try {
            List<ConstraintViolation> errors = systemMgrService.importIssueStatus(file.getInputStream());
            if (!errors.isEmpty()) {
                manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
                manager.addValidations(errors);
            }
        } catch (Exception e) {
            manager = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            manager.addDataObject("导入的文件不符合要求");
        }
        return manager.respond();
    }

    private void verifyRequestIssueStatus(String statusCode) {
        IssueStatus issueStatus = (IssueStatus) SessionManager.getAttribute(SESSION_KEY_ISS);
        if (issueStatus == null || !statusCode.equals(issueStatus.getStatusCode())) {
            throw new PermissionDeniedException();
        }
    }
}
