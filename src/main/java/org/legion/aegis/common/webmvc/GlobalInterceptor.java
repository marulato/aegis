package org.legion.aegis.common.webmvc;

import org.legion.aegis.common.AppContext;
import org.legion.aegis.common.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class GlobalInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(GlobalInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession();
        Object context = session.getAttribute(AppContext.APP_CONTEXT_KEY);
        if (context != null) {
            SessionManager.setSession(request);
            AppContext.setWebThreadAppContext((AppContext) context);
            return true;
        } else {
            response.sendRedirect("/web/login");
            log.warn("Intercepted request: " + request.getRequestURL());
        }
        return false;
    }

}
