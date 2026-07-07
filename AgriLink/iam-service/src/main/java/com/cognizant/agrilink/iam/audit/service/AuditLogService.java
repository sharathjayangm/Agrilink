package com.cognizant.agrilink.iam.audit.service;

import com.cognizant.agrilink.iam.audit.dto.AuditLogDto;
import com.cognizant.agrilink.iam.audit.entity.AuditLog;
import com.cognizant.agrilink.iam.audit.repository.AuditLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final ObjectMapper objectMapper;

    /**
     * Log an audit event with all details
     */
    public AuditLogDto logAuditEvent(Integer userId, String module, String action, String entityType,
                                      String entityId, Object before, Object after, String ipAddress,
                                      String sessionId, String description) {
        return logAuditEvent(userId, module, action, entityType, entityId, before, after, ipAddress, sessionId, description, null);
    }

    /**
     * Log an audit event with error details
     */
    public AuditLogDto logAuditEvent(Integer userId, String module, String action, String entityType,
                                      String entityId, Object before, Object after, String ipAddress,
                                      String sessionId, String description, Exception exception) {
        try {
            String eventId = generateEventId();
            LocalDateTime timestamp = LocalDateTime.now();

            String beforeJson = before != null ? objectMapper.writeValueAsString(before) : "{}";
            String afterJson = after != null ? objectMapper.writeValueAsString(after) : "{}";
            String changesJson = calculateChanges(beforeJson, afterJson);

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .module(module)
                    .action(action)
                    .entityType(entityType)
                    .entityId(entityId)
                    .before(beforeJson)
                    .after(afterJson)
                    .changes(changesJson)
                    .eventId(eventId)
                    .ipAddress(ipAddress)
                    .timestamp(timestamp)
                    .sessionId(sessionId)
                    .description(description)
                    .status(exception == null ? "SUCCESS" : "FAILURE")
                    .errorMessage(exception != null ? exception.getMessage() : null)
                    .build();

            AuditLog savedAuditLog = auditLogRepository.save(auditLog);
            log.info("Audit log created: EventID={}, Module={}, Action={}, User={}", 
                    eventId, module, action, userId);

            return convertToDto(savedAuditLog);
        } catch (Exception e) {
            log.error("Error creating audit log: ", e);
            throw new RuntimeException("Failed to create audit log", e);
        }
    }

    /**
     * Get all audit logs for a user
     */
    public List<AuditLogDto> getAuditLogsByUser(Integer userId) {
        return auditLogRepository.findByUserId(userId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all audit logs for a module
     */
    public List<AuditLogDto> getAuditLogsByModule(String module) {
        return auditLogRepository.findByModule(module).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all audit logs for an action
     */
    public List<AuditLogDto> getAuditLogsByAction(String action) {
        return auditLogRepository.findByAction(action).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs by entity type
     */
    public List<AuditLogDto> getAuditLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs by entity ID
     */
    public List<AuditLogDto> getAuditLogsByEntityId(String entityId) {
        return auditLogRepository.findByEntityId(entityId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get audit log by event ID
     */
    public AuditLogDto getAuditLogByEventId(String eventId) {
        return auditLogRepository.findByEventId(eventId)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Audit log not found with eventId: " + eventId));
    }

    /**
     * Get audit logs within date range
     */
    public List<AuditLogDto> getAuditLogsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByDateRange(startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs for user within date range
     */
    public List<AuditLogDto> getAuditLogsByUserAndDateRange(Integer userId, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByUserIdAndDateRange(userId, startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs for module within date range
     */
    public List<AuditLogDto> getAuditLogsByModuleAndDateRange(String module, LocalDateTime startDate, LocalDateTime endDate) {
        return auditLogRepository.findByModuleAndDateRange(module, startDate, endDate).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs for module and action
     */
    public List<AuditLogDto> getAuditLogsByModuleAndAction(String module, String action) {
        return auditLogRepository.findByModuleAndAction(module, action).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get audit logs by entity type and action
     */
    public List<AuditLogDto> getAuditLogsByEntityTypeAndAction(String entityType, String action) {
        return auditLogRepository.findByEntityTypeAndAction(entityType, action).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get all audit logs
     */
    public List<AuditLogDto> getAllAuditLogs() {
        return auditLogRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * Get audit log by ID
     */
    public AuditLogDto getAuditLogById(Integer auditLogId) {
        return auditLogRepository.findById(auditLogId)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Audit log not found with id: " + auditLogId));
    }

    /**
     * Generate unique event ID
     */
    private String generateEventId() {
        String prefix = "evt-" + LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return prefix + "-" + uuid;
    }

    /**
     * Calculate changes between before and after states
     */
    private String calculateChanges(String beforeJson, String afterJson) {
        try {
            if (beforeJson == null || beforeJson.equals("{}")) {
                return afterJson;
            }
            if (afterJson == null || afterJson.equals("{}")) {
                return "{}";
            }
            // In a real implementation, you would compare the two JSON objects
            // and return only the changed fields
            return afterJson;
        } catch (Exception e) {
            log.error("Error calculating changes: ", e);
            return "{}";
        }
    }

    /**
     * Convert AuditLog entity to DTO
     */
    private AuditLogDto convertToDto(AuditLog auditLog) {
        return AuditLogDto.builder()
                .auditLogId(auditLog.getAuditLogId())
                .userId(auditLog.getUserId())
                .sessionId(auditLog.getSessionId())
                .module(auditLog.getModule())
                .action(auditLog.getAction())
                .entityType(auditLog.getEntityType())
                .entityId(auditLog.getEntityId())
                .before(auditLog.getBefore())
                .after(auditLog.getAfter())
                .changes(auditLog.getChanges())
                .eventId(auditLog.getEventId())
                .ipAddress(auditLog.getIpAddress())
                .timestamp(auditLog.getTimestamp())
                .description(auditLog.getDescription())
                .status(auditLog.getStatus())
                .errorMessage(auditLog.getErrorMessage())
                .build();
    }
}
