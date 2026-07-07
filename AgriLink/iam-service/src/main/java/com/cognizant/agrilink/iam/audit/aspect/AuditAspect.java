package com.cognizant.agrilink.iam.audit.aspect;

import com.cognizant.agrilink.iam.audit.annotation.Audit;
import com.cognizant.agrilink.iam.audit.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.UUID;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditLogService auditLogService;

    /**
     * Intercept methods annotated with @Audit and log audit events
     */
    @Around("@annotation(audit)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Audit audit) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        Exception exception = null;

        try {
            result = joinPoint.proceed();
            return result;
        } catch (Exception e) {
            exception = e;
            throw e;
        } finally {
            try {
                long duration = System.currentTimeMillis() - startTime;
                auditOperation(joinPoint, audit, result, exception, duration);
            } catch (Exception e) {
                log.error("Error logging audit: ", e);
            }
        }
    }

    /**
     * Perform the actual audit logging
     */
    private void auditOperation(ProceedingJoinPoint joinPoint, Audit audit, Object result, Exception exception, long duration) {
        Integer userId = getCurrentUserId();
        String ipAddress = getClientIpAddress();
        String sessionId = getSessionId();

        String module = audit.module();
        String action = audit.action();
        String entityType = audit.entityType();
        String description = String.format("%s - %s operation on %s (Duration: %dms)", 
                module, action, entityType, duration);

        // Extract entity ID from method arguments if provided
        String entityId = extractEntityId(joinPoint, audit);

        // Extract before state (for updates and deletes)
        Object beforeState = extractBeforeState(joinPoint, audit);

        // Extract after state (from result for creates and updates)
        Object afterState = extractAfterState(result, audit);

        // Log the audit event
        auditLogService.logAuditEvent(
                userId,
                module,
                action,
                entityType,
                entityId,
                beforeState,
                afterState,
                ipAddress,
                sessionId,
                description,
                exception
        );
    }

    /**
     * Get current user ID from Security Context
     */
    private Integer getCurrentUserId() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                if (principal instanceof com.cognizant.agrilink.iam.identityAccess.model.UserDetails) {
                    return ((com.cognizant.agrilink.iam.identityAccess.model.UserDetails) principal).getId();
                }
            }
        } catch (Exception e) {
            log.debug("Unable to get user from security context: ", e);
        }
        return 0; // Default for system actions
    }

    /**
     * Get client IP address from request
     */
    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String clientIp = request.getHeader("X-Forwarded-For");
                if (clientIp == null || clientIp.isEmpty()) {
                    clientIp = request.getRemoteAddr();
                }
                return clientIp;
            }
        } catch (Exception e) {
            log.debug("Unable to get client IP: ", e);
        }
        return "UNKNOWN";
    }

    /**
     * Get session ID from request
     */
    private String getSessionId() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String sessionId = request.getSession(false) != null ? request.getSession(false).getId() : null;
                return sessionId != null ? sessionId : UUID.randomUUID().toString();
            }
        } catch (Exception e) {
            log.debug("Unable to get session ID: ", e);
        }
        return UUID.randomUUID().toString();
    }

    /**
     * Extract entity ID from method arguments
     */
    private String extractEntityId(ProceedingJoinPoint joinPoint, Audit audit) {
        if (!audit.entityIdIndex().isEmpty()) {
            try {
                int index = Integer.parseInt(audit.entityIdIndex());
                Object arg = joinPoint.getArgs()[index];
                return arg != null ? arg.toString() : null;
            } catch (Exception e) {
                log.debug("Unable to extract entity ID from argument", e);
            }
        }
        return null;
    }

    /**
     * Extract before state from method arguments
     */
    private Object extractBeforeState(ProceedingJoinPoint joinPoint, Audit audit) {
        if (!audit.beforeStateIndex().isEmpty()) {
            try {
                int index = Integer.parseInt(audit.beforeStateIndex());
                return joinPoint.getArgs()[index];
            } catch (Exception e) {
                log.debug("Unable to extract before state from argument", e);
            }
        }
        return null;
    }

    /**
     * Extract after state from method result
     */
    private Object extractAfterState(Object result, Audit audit) {
        if (result != null && !audit.captureResult().isEmpty() && Boolean.parseBoolean(audit.captureResult())) {
            return result;
        }
        return null;
    }
}
