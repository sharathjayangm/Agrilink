package com.cognizant.agrilink.iam.audit.annotation;

import java.lang.annotation.*;

/**
 * Annotation to mark methods for audit logging
 * When a method is annotated with @Audit, the AuditAspect will automatically
 * log the operation with module, action, entity details, and before/after states.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Audit {

    /**
     * The module name (e.g., "IDENTITY", "FARMER", "CROP")
     */
    String module();

    /**
     * The action performed (e.g., "CREATE", "UPDATE", "DELETE")
     */
    String action();

    /**
     * The entity type being operated on (e.g., "user_details", "farmer_profile")
     */
    String entityType();

    /**
     * The index of the entity ID in method parameters (optional)
     * If empty, entity ID will be extracted from result
     */
    String entityIdIndex() default "";

    /**
     * The index of the before state object in method parameters (optional)
     * Typically used for UPDATE operations
     */
    String beforeStateIndex() default "";

    /**
     * Whether to capture the result as the after state (default: true for creates)
     */
    String captureResult() default "false";
}
