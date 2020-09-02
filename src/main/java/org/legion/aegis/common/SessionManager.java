package org.legion.aegis.common;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.InetAddress;

public class SessionManager {

    public static HttpSession getSession() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes)
                RequestContextHolder.currentRequestAttributes();
        return requestAttributes.getRequest().getSession();
    }

    public static HttpSession getSession(HttpServletRequest request) {
        return request.getSession(false);
    }

    public static void setAttribute(String name, Object object) {
        if (name != null && object != null) {
            getSession().setAttribute(name, object);
        }
    }

    public static Object getAttribute(String name) {
        return getSession().getAttribute(name);
    }

    public static void removeAttribute(String name) {
        getSession().removeAttribute(name);
    }



    public static String getIpAddress(HttpServletRequest request) {
        if (request != null) {
            String ip = request.getHeader("x-forwarded-for");
            if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
                ip = request.getHeader ("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length () == 0 || "unknown".equalsIgnoreCase (ip)) {
                ip = request.getRemoteAddr ();
                if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                    try {
                        ip = InetAddress.getLocalHost ().getHostAddress();
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            }
            if (ip != null && ip.length () > 15) {
                if (ip.indexOf (",") > 0) {
                    ip = ip.substring (0, ip.indexOf (","));
                }
            }
            return ip;
        }
        return null;
    }
}
