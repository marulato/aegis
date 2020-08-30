package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.consts.LoginStatus;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.entity.UserRole;
import org.legion.aegis.admin.service.PortalLoginService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.LogUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class PortalLoginController {

    private final PortalLoginService loginService;
    private final UserAccountService accountService;

    private static final Logger log = LoggerFactory.getLogger(PortalLoginController.class);

    @Autowired
    public PortalLoginController(PortalLoginService loginService, UserAccountService accountService) {
        this.loginService = loginService;
        this.accountService = accountService;
    }

    /**
     * Web View Call
     * @return Login page
     */
    @GetMapping("/web/login")
    public String getLoginPage(HttpServletRequest request) {
        log.info(LogUtils.around("Enter login page"));
        return "admin/login";
    }

    @GetMapping("/web/index")
    public String getDefaultIndexPage(HttpServletRequest request) {
        log.info(LogUtils.around("Enter default Landing page"));
        return "index";
    }

    /**
     * Ajax Call
     * @return login validation result
     */
    @PostMapping("/web/login")
    @ResponseBody
    public AjaxResponseBody login(UserAccount webUser, HttpServletRequest request) throws Exception {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
        Map<String, List<String>> errorMap = CommonValidator.doValidation(webUser, null);
        if (!errorMap.isEmpty()) {
            responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            responseMgr.addValidations(errorMap);
        } else {
            LoginStatus loginStatus = loginService.login(webUser, request);
            if (loginStatus == LoginStatus.SUCCESS) {
                responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
                AppContext context = AppContext.getAppContext(request);
                //user has multiple roles, should choose a role manually to proceed
                if (context.getAllRoles().size() > 1) {
                    responseMgr.addDataObjects(context.getAllRoles());
                } else {
                    responseMgr.addDataObject(0);
                }
            } else if (loginStatus == LoginStatus.ACCOUNT_EXPIRED) {
                responseMgr.addError("userId", "账户已过期");
            } else if (loginStatus == LoginStatus.ACCOUNT_LOCKED) {
                responseMgr.addError("userId", "账户已锁定");
            } else if (loginStatus == LoginStatus.ACCOUNT_INACTIVE) {
                responseMgr.addError("userId", "账户尚未启用");
            } else if (loginStatus == LoginStatus.ACCOUNT_FROZEN) {
                responseMgr.addError("userId", "账户已冻结");
            } else {
                responseMgr.addError("userId", "");
                responseMgr.addError("password", "用户名或密码不正确");
            }
        }
        return responseMgr.respond();
    }

    @GetMapping("/web/login/landing/{roleId}")
    public String getLandingPage(@PathVariable String roleId, HttpServletRequest request) {
        AppContext context = AppContext.getAppContext(request);
        if (context == null) {
            return "redirect:/web/login";
        }
        context.setCurrentRole(null);
        if (StringUtils.isNotBlank(roleId) && context.getAllRoles() != null && context.getAllRoles().size() > 1) {
            context.setLoggedIn(true);
            for (UserRole role : context.getAllRoles()) {
                if (roleId.equals(role.getId())) {
                    context.setCurrentRole(role);
                    break;
                }
            }
        } else if (context.getAllRoles() != null && context.getAllRoles().size() == 1) {
            context.setLoggedIn(true);
            context.setCurrentRole(context.getAllRoles().get(0));
        }
        if (context.getCurrentRole() == null) {
            throw new PermissionDeniedException();
        } else {
            UserAccount userAccount = accountService.getUserByLoginId(context.getLoginId());
            request.getSession().setAttribute("profileName", userAccount.getDisplayName());
            request.getSession().setAttribute("roleId", context.getCurrentRole().getId());
            return "redirect:" + context.getCurrentRole().getLandingPage();
        }
    }

    @GetMapping("/web/login/selectRole")
    @ResponseBody
    @RequiresLogin
    public AjaxResponseBody selectRole(HttpServletRequest request) {
        AppContext appContext = AppContext.getAppContext(request);
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        if (appContext != null) {
            responseMgr.addDataObjects(appContext.getAllRoles());
        }
        return responseMgr.respond();
    }

    @GetMapping("/web/logout")
    @RequiresLogin
    public String logout(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/web/login";
    }
}
