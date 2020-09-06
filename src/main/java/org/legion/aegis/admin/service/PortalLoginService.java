package org.legion.aegis.admin.service;

import org.legion.aegis.admin.consts.LoginStatus;
import org.legion.aegis.admin.entity.UserAccount;
import org.legion.aegis.admin.entity.UserLoginHistory;
import org.legion.aegis.admin.entity.UserRole;
import org.legion.aegis.admin.entity.UserRoleAssign;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.ConfigUtils;
import org.legion.aegis.common.utils.DateUtils;
import org.legion.aegis.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
public class PortalLoginService {

    private final UserAccountService userAcctService;

    @Autowired
    public PortalLoginService(UserAccountService userAcctService) {
        this.userAcctService = userAcctService;
    }

    @Transactional(rollbackFor = Exception.class)
    public LoginStatus login(UserAccount webUser, HttpServletRequest request) {
        LoginStatus status = null;
        if (webUser != null) {
            UserAccount user = userAcctService.getUserByLoginId(webUser.getLoginId());
            if (user != null) {
                AppContext appContext = new AppContext();
                appContext.setLoginId(user.getLoginId());
                appContext.setDomain(user.getDomain());
                appContext.setAppContext(request);
                appContext.setUserId(user.getId());
                String accountStatus = checkStatus(user);
                if (AppConsts.ACCOUNT_STATUS_ACTIVE.equals(accountStatus)) {
                    boolean isPwdMatch = userAcctService.isPasswordMatch(webUser.getPassword(), user.getPassword());
                    if (isPwdMatch) {
                        status = LoginStatus.SUCCESS;
                        List<UserRoleAssign> userRoleAssigns = userAcctService.getActiveRoleAssignById(user.getId());
                        List<UserRole> roles = new ArrayList<>();
                        for (UserRoleAssign userRoleAssign : userRoleAssigns) {
                            UserRole role = userAcctService.getRoleById(userRoleAssign.getRoleId());
                            roles.add(role);
                        }
                        webUser.setIsFirstLogin(user.getIsFirstLogin());
                        appContext.setAllRoles(roles);
                    } else {
                        status = LoginStatus.INVALID_PASS;
                    }
                } else if (AppConsts.ACCOUNT_STATUS_INACTIVE.equals(accountStatus)) {
                    status = LoginStatus.ACCOUNT_INACTIVE;
                } else if (AppConsts.ACCOUNT_STATUS_EXPIRED.equals(accountStatus)) {
                    status = LoginStatus.ACCOUNT_EXPIRED;
                } else if (AppConsts.ACCOUNT_STATUS_LOCKED.equals(accountStatus)) {
                    status = LoginStatus.ACCOUNT_LOCKED;
                } else if (AppConsts.ACCOUNT_STATUS_FROZEN.equals(accountStatus)) {
                    status = LoginStatus.ACCOUNT_FROZEN;
                } else if (AppConsts.ACCOUNT_STATUS_VOIDED.equals(accountStatus)) {
                    status = LoginStatus.VOIDED;
                }
                logLogin(user, status, request);
            } else {
                status = LoginStatus.ACCOUNT_NOT_EXIST;
            }
        }
        return status;
    }


    public void logLogin(UserAccount user, LoginStatus status, HttpServletRequest request) {
        if (user != null && status != null) {
            user.setLastLoginAttemptDt(DateUtils.now());
            user.setLastLoginIp(user.getLastLoginIp());
            if (status == LoginStatus.SUCCESS) {
                user.setLastLoginIp(SessionManager.getIpAddress(request));
                user.setLastLoginSuccessDt(user.getLastLoginAttemptDt());
                user.setLoginFailedTimes(0);
                //user.setIsFirstLogin(AppConsts.NO);
            } else if (status == LoginStatus.INVALID_PASS) {
                user.setLoginFailedTimes(user.getLoginFailedTimes() + 1);
                Integer maxFailedTimes = 0;
                String maxFailedTimesStr = ConfigUtils.get("security.login.maxFailedTimes");
                if (StringUtils.isBlank(maxFailedTimesStr) || !StringUtils.isInteger(maxFailedTimesStr)
                        || Integer.parseInt(maxFailedTimesStr) <= 1 ) {
                    maxFailedTimes = 10;
                } else {
                    maxFailedTimes = StringUtils.parseIfIsInteger(maxFailedTimesStr);
                }
                if (maxFailedTimes != null && user.getLoginFailedTimes() >= maxFailedTimes) {
                    user.setStatus(AppConsts.ACCOUNT_STATUS_LOCKED);
                }
            } else {
                user.setLoginFailedTimes(user.getLoginFailedTimes() + 1);
            }
            UserLoginHistory loginHistory = new UserLoginHistory();
            loginHistory.setUserAcctId(user.getId());
            loginHistory.setAcctStatus(user.getStatus());
            loginHistory.setIpAddress(user.getLastLoginIp());
            loginHistory.setLoginAt(user.getLastLoginAttemptDt());
            loginHistory.setLoginStatus(status.getValue());
            loginHistory.setIpAddress(SessionManager.getIpAddress(request));
            loginHistory.setBrowser(request.getParameter("browser"));
            JPAExecutor.save(loginHistory);
            JPAExecutor.update(user);
        }
    }

    private String checkStatus(UserAccount user) {
        if (!userAcctService.isActive(user)) {
            user.setStatus(AppConsts.ACCOUNT_STATUS_EXPIRED);
        }
        if (userAcctService.isActive(user) && AppConsts.ACCOUNT_STATUS_INACTIVE.equals(user.getStatus())) {
            user.setStatus(AppConsts.ACCOUNT_STATUS_ACTIVE);
        }
        if (AppConsts.ACCOUNT_STATUS_LOCKED.equals(user.getStatus()) &&
                DateUtils.getHoursBetween(user.getLastLoginAttemptDt(), DateUtils.now()) >= 24) {
            user.setStatus(userAcctService.isActive(user) ?
                    AppConsts.ACCOUNT_STATUS_ACTIVE : AppConsts.ACCOUNT_STATUS_EXPIRED);
        }
        return user.getStatus();
    }
}
