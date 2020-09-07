package org.legion.aegis.common.aop.permission;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.legion.aegis.admin.entity.UserRole;
import org.legion.aegis.common.AppContext;
import org.legion.aegis.general.ex.PermissionDeniedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
public class PermissionAspect {

    private static final Logger log = LoggerFactory.getLogger(PermissionAspect.class);
    @Around("@annotation(org.legion.aegis.common.aop.permission.RequiresRoles)")
    public Object checkRequiresRoles(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean hasPermission = true;
        Class<?> targetType = joinPoint.getTarget().getClass();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = targetType.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        RequiresRoles requiresRoles = method.getAnnotation(RequiresRoles.class);
        List<String> roleIds = Arrays.asList(requiresRoles.value());
        Logical logical = requiresRoles.logical();
        AppContext context = AppContext.getFromWebThread();
        if (context != null && context.isLoggedIn()) {
            log.info("Checking User -> " + context.getLoginId());
            List<String> allRoles = new ArrayList<>();
            for (UserRole role : context.getAllRoles()) {
                allRoles.add(role.getId());
            }
            if (logical == Logical.AND) {
                if (!allRoles.containsAll(roleIds)) {
                    hasPermission = false;
                }
            } else if (logical == Logical.OR) {
                if (!roleIds.contains(context.getCurrentRole().getId())) {
                    hasPermission = false;
                }
            } else if(logical == Logical.NONE) {
                if (roleIds.contains(context.getCurrentRole().getId())) {
                    hasPermission = false;
                }
            }
            if (hasPermission) {
                return joinPoint.proceed();
            } else {
                throw new PermissionDeniedException();
            }
        } else {
            throw new PermissionDeniedException();
        }
    }

    @Around("@annotation(org.legion.aegis.common.aop.permission.RequiresLogin)")
    public Object checkRequiresLogin(ProceedingJoinPoint joinPoint) throws Throwable {
        Class<?> targetType = joinPoint.getTarget().getClass();
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = targetType.getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
        RequiresLogin requiresLogin = method.getAnnotation(RequiresLogin.class);
        AppContext context = AppContext.getFromWebThread();
        if (context == null || !context.isLoggedIn()) {
            throw new PermissionDeniedException();
        } else {
            return joinPoint.proceed();
        }
    }
}
