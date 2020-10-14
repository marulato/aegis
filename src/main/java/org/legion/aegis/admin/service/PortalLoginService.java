package org.legion.aegis.admin.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.legion.aegis.admin.consts.LoginStatus;
import org.legion.aegis.admin.dao.UserAccountDAO;
import org.legion.aegis.admin.entity.*;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.legion.aegis.common.consts.AppConsts;
import org.legion.aegis.common.jpa.exec.JPAExecutor;
import org.legion.aegis.common.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class PortalLoginService {

    private final UserAccountService userAcctService;
    private final Cache<Long, String> tokenCache;

    @Autowired
    public PortalLoginService(UserAccountService userAcctService) {
        this.userAcctService = userAcctService;
        tokenCache = CacheBuilder.newBuilder().build();
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
                        tokenCache.put(user.getId(), MiscGenerator.generateToken());
                        if (userRoleAssigns.isEmpty()) {
                            status = LoginStatus.ACCOUNT_EXPIRED;
                        } else {
                            webUser.setIsFirstLogin(user.getIsFirstLogin());
                            appContext.setAllRoles(roles);
                            appContext.setAssignments(userAcctService.getUserProjectAssignments(user.getId()));
                            appContext.setName(user.getName());
                            SessionManager.setAttribute("token", tokenCache.getIfPresent(user.getId()));
                        }
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
                UnknownLoginHistory unHistory = new UnknownLoginHistory();
                if (webUser.getLoginId().length() > 32) {
                    webUser.setLoginId(webUser.getLoginId().substring(0, 32));
                }
                Date now = new Date();
                unHistory.setLoginId(webUser.getLoginId());
                unHistory.setIpAddress(SessionManager.getIpAddress(request));
                unHistory.setLoginAt(now);
                unHistory.setBrowser(request.getParameter("browser"));
                unHistory.setCreatedAt(now);
                unHistory.setUpdatedAt(now);
                unHistory.setCreatedBy("VIRTUAL_LOGIN");
                unHistory.setUpdatedBy("VIRTUAL_LOGIN");
                SpringUtils.getBean(UserAccountDAO.class).createUnknownHistory(unHistory);
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

    public String getUserToken(Long userId) {
        return tokenCache.getIfPresent(userId);
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
