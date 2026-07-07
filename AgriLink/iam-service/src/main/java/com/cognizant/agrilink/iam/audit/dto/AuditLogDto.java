package com.cognizant.agrilink.iam.audit.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDto {

    private Integer auditLogId;
    private Integer userId;
    private String sessionId;
    private String module;
    private String action;
    private String entityType;
    private String entityId;
    private String before;
    private String after;
    private String changes;
    private String eventId;
    private String ipAddress;
    private LocalDateTime timestamp;
    private String description;
    private String status;
    private String errorMessage;
}
