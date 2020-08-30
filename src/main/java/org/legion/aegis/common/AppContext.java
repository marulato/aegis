package org.legion.aegis.common;


import org.legion.aegis.admin.entity.UserRole;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.condition.RequestConditionHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

public class AppContext implements Serializable {

    private Long userId;
    private String loginId;
    private String domain;
    private String userType;
    private boolean isAdminRole;
    private boolean hasAdminRole;
    private boolean loggedIn;
    private UserRole currentRole;
    private List<UserRole> allRoles;

    public static final String APP_CONTEXT_KEY = "Legion_Web_Session_Context";
    private static final ThreadLocal<AppContext> localContext = new ThreadLocal<>();

    public static AppContext getFromWebThread() {

        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        return (AppContext) requestAttributes.getRequest().getSession().getAttribute(APP_CONTEXT_KEY);
    }

    public static AppContext getAppContext(HttpServletRequest request) {
        if (request != null) {
            Object obj = request.getSession().getAttribute(APP_CONTEXT_KEY);
            if (obj != null) {
                return (AppContext) obj;
            }
        }
        return null;
    }

    public void setAppContext() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        requestAttributes.getRequest().getSession().setAttribute(APP_CONTEXT_KEY, this);
    }

    public void setAppContext(HttpServletRequest request) {
        if (request != null) {
            request.getSession().setAttribute(APP_CONTEXT_KEY, this);
        }
    }

    public void setLocalAppContext() {
        localContext.set(this);
    }

    public AppContext getLocalAppContext() {
        return localContext.get();
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public boolean isAdminRole() {
        return isAdminRole;
    }

    public void setAdminRole(boolean adminRole) {
        isAdminRole = adminRole;
    }

    public boolean isHasAdminRole() {
        return hasAdminRole;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public void setHasAdminRole(boolean hasAdminRole) {
        this.hasAdminRole = hasAdminRole;
    }

    public UserRole getCurrentRole() {
        return currentRole;
    }

    public void setCurrentRole(UserRole currentRole) {
        this.currentRole = currentRole;
    }

    public List<UserRole> getAllRoles() {
        return allRoles;
    }

    public void setAllRoles(List<UserRole> allRoles) {
        this.allRoles = allRoles;
    }
}
