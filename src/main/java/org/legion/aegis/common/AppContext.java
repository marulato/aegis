package org.legion.aegis.common;


import org.legion.aegis.admin.entity.UserRole;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.util.List;

public class AppContext implements Serializable {

    private String loginId;
    private String domain;
    private String userType;
    private boolean isAdminRole;
    private boolean hasAdminRole;
    private boolean loggedIn;
    private UserRole currentRole;
    private List<UserRole> allRoles;

    public static final String APP_CONTEXT_KEY = "Legion_Web_Session_Context";
    private static final ThreadLocal<AppContext> webThreadContext = new ThreadLocal<>();
    private static final ThreadLocal<AppContext> localThreadContext = new ThreadLocal<>();

    public static AppContext getFromWebThread(boolean flag) {
        AppContext context = webThreadContext.get();
        if (context == null) {
            HttpSession session = SessionManager.getSession();
            if (session != null) {
                context = (AppContext) session.getAttribute(APP_CONTEXT_KEY);
                setWebThreadAppContext(context);
            }
        }
        if (context == null) {
            context = localThreadContext.get();
        }
        if (context == null && flag) {
            context = new AppContext();
            context.setLoginId("Virtual User Account");
            localThreadContext.set(context);
        }
        return context;
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

    public static AppContext getFromWebThread() {
        return getFromWebThread(false);
    }

    public static void setLocalThreadAppContext(AppContext context) {
        if (context != null) {
            localThreadContext.set(context);
        }
    }

    public static void setWebThreadAppContext(AppContext context) {
        if (context != null) {
            HttpSession session = SessionManager.getSession();
            session.setAttribute(APP_CONTEXT_KEY, context);
            webThreadContext.set(context);
        }
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
