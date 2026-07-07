package com.cognizant.agrilink.iam.audit.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@Slf4j
public class AuditInterceptor implements HandlerInterceptor {

    private static final String AUDIT_START_TIME = "auditStartTime";
    private static final String AUDIT_REQUEST_METHOD = "auditRequestMethod";
    private static final String AUDIT_REQUEST_URI = "auditRequestURI";

    /**
     * Called before the actual handler execution
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(AUDIT_START_TIME, System.currentTimeMillis());
        request.setAttribute(AUDIT_REQUEST_METHOD, request.getMethod());
        request.setAttribute(AUDIT_REQUEST_URI, request.getRequestURI());

        log.debug("Audit Interceptor: {} {}", request.getMethod(), request.getRequestURI());
        return true;
    }

    /**
     * Called after the view is rendered
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            long startTime = (Long) request.getAttribute(AUDIT_START_TIME);
            long duration = System.currentTimeMillis() - startTime;
            String method = (String) request.getAttribute(AUDIT_REQUEST_METHOD);
            String uri = (String) request.getAttribute(AUDIT_REQUEST_URI);

            log.debug("Request completed: {} {} - Status: {} - Duration: {}ms",
                    method, uri, response.getStatus(), duration);
        } catch (Exception e) {
            log.error("Error in audit interceptor afterCompletion: ", e);
        }
    }
}
