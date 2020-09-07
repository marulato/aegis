package org.legion.aegis.admin.controller;

import org.legion.aegis.admin.consts.LoginStatus;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.entity.UserRole;
import org.legion.aegis.admin.service.PortalLoginService;
import org.legion.aegis.admin.service.UserAccountService;
import org.legion.aegis.admin.validator.ResetPasswordValidator;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.aop.permission.RequiresLogin;
import org.legion.aegis.common.base.AjaxResponseBody;
import org.legion.aegis.common.base.AjaxResponseManager;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.utils.LogUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.legion.aegis.common.validation.CommonValidator;
import org.legion.aegis.common.validation.ConstraintViolation;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class PortalLoginController {

    private final PortalLoginService loginService;
    private final UserAccountService accountService;
    private static final String SESSION_KEY = "SESSION_USER";

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
    public String getLoginPage() {
        log.info(LogUtils.around("Enter login page"));
        return "admin/login";
    }

    @GetMapping("/web/index")
    public String getDefaultIndexPage(HttpServletRequest request) {
        log.info(LogUtils.around("Enter default Landing page"));
        return "index";
    }

    @GetMapping("/web/login/changePassword")
    public String getChangePasswordPage(HttpServletRequest request) {
        UserAccount userAccount = (UserAccount) SessionManager.getAttribute(SESSION_KEY);
        if (userAccount != null && AppConsts.YES.equals(userAccount.getIsFirstLogin())) {
            return "admin/loginChangePwd";
        }
        request.getSession().invalidate();
        return "redirect:/web/login";
    }

    /**
     * Ajax Call
     * @return login validation result
     */
    @PostMapping("/web/login")
    @ResponseBody
    public AjaxResponseBody login(UserAccount webUser, HttpServletRequest request) throws Exception {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
        List<ConstraintViolation> violations = CommonValidator.validate(webUser, null);
        if (!violations.isEmpty()) {
            responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            responseMgr.addValidations(violations);
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
                if (AppConsts.YES.equals(webUser.getIsFirstLogin())) {
                    responseMgr.addDataObject("FirstLogin");
                    context.setLoggedIn(false);
                    SessionManager.setAttribute(SESSION_KEY, webUser);
                }
            } else if (loginStatus == LoginStatus.ACCOUNT_EXPIRED) {
                responseMgr.addError("loginId", "账户已过期");
            } else if (loginStatus == LoginStatus.ACCOUNT_LOCKED) {
                responseMgr.addError("loginId", "账户已锁定");
            } else if (loginStatus == LoginStatus.ACCOUNT_INACTIVE) {
                responseMgr.addError("loginId", "账户尚未启用");
            } else if (loginStatus == LoginStatus.ACCOUNT_FROZEN) {
                responseMgr.addError("loginId", "账户已冻结");
            } else {
                responseMgr.addError("loginId", "");
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

    @PostMapping("/web/login/changePassword")
    @ResponseBody
    public AjaxResponseBody changePwdForFirstLogin(@RequestBody Map<String, Object> params) {
        AjaxResponseManager responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_SUCCESS);
        Map<String, String> errors = new ResetPasswordValidator().doValidate(params);
        if (!errors.isEmpty()) {
            responseMgr = AjaxResponseManager.create(AppConsts.RESPONSE_VALIDATION_NOT_PASS);
            responseMgr.addErrors(errors);
        } else {
            UserAccount userAccount = (UserAccount) SessionManager.getAttribute(SESSION_KEY);
            if (userAccount != null) {
                UserAccount user = accountService.getUserByLoginId(userAccount.getLoginId());
                userAccount.setIsFirstLogin(AppConsts.NO);
                user.setIsFirstLogin(AppConsts.NO);
                accountService.resetPassord(user, (String) params.get("pwd1"));
            }
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
