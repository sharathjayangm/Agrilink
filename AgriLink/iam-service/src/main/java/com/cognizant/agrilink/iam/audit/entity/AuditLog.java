package com.cognizant.agrilink.iam.audit.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "AuditLog")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AuditLogID")
    private Integer auditLogId;

    @Column(name = "UserID", nullable = false)
    private Integer userId;

    @Column(name = "SessionID", length = 100)
    private String sessionId;

    @Column(name = "Module", nullable = false, length = 50)
    private String module;

    @Column(name = "Action", nullable = false, length = 20)
    private String action; // CREATE, UPDATE, DELETE, READ, LOGIN, LOGOUT

    @Column(name = "EntityType", nullable = false, length = 100)
    private String entityType;

    @Column(name = "EntityID", length = 50)
    private String entityId;

    @Column(name = "Before", columnDefinition = "JSON")
    private String before;

    @Column(name = "After", columnDefinition = "JSON")
    private String after;

    @Column(name = "Changes", columnDefinition = "JSON")
    private String changes;

    @Column(name = "EventID", unique = true, nullable = false, length = 50)
    private String eventId;

    @Column(name = "IPAddress", length = 100)
    private String ipAddress;

    @Column(name = "Timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "Description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "Status", length = 20)
    private String status; // SUCCESS, FAILURE

    @Column(name = "ErrorMessage", columnDefinition = "TEXT")
    private String errorMessage;
}
