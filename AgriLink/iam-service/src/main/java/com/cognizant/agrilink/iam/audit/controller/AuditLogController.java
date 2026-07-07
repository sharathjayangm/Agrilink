package com.cognizant.agrilink.iam.audit.controller;

import com.cognizant.agrilink.iam.audit.dto.AuditLogDto;
import com.cognizant.agrilink.iam.audit.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/agrilink/iam/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogService auditLogService;

    /**
     * GET /agrilink/iam/audit-logs - Get all audit logs
     */
    @GetMapping
    public ResponseEntity<List<AuditLogDto>> getAllAuditLogs() {
        return ResponseEntity.ok(auditLogService.getAllAuditLogs());
    }

    /**
     * GET /agrilink/iam/audit-logs/{id} - Get audit log by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<AuditLogDto> getAuditLogById(@PathVariable Integer id) {
        return ResponseEntity.ok(auditLogService.getAuditLogById(id));
    }

    /**
     * GET /agrilink/iam/audit-logs/user/{userId} - Get audit logs by user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUser(userId));
    }

    /**
     * GET /agrilink/iam/audit-logs/module/{module} - Get audit logs by module
     */
    @GetMapping("/module/{module}")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByModule(@PathVariable String module) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByModule(module));
    }

    /**
     * GET /agrilink/iam/audit-logs/action/{action} - Get audit logs by action
     */
    @GetMapping("/action/{action}")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByAction(@PathVariable String action) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByAction(action));
    }

    /**
     * GET /agrilink/iam/audit-logs/entity-type/{entityType} - Get audit logs by entity type
     */
    @GetMapping("/entity-type/{entityType}")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByEntityType(@PathVariable String entityType) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByEntityType(entityType));
    }

    /**
     * GET /agrilink/iam/audit-logs/entity/{entityId} - Get audit logs by entity ID
     */
    @GetMapping("/entity/{entityId}")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByEntityId(@PathVariable String entityId) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByEntityId(entityId));
    }

    /**
     * GET /agrilink/iam/audit-logs/event/{eventId} - Get audit log by event ID
     */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<AuditLogDto> getAuditLogByEventId(@PathVariable String eventId) {
        return ResponseEntity.ok(auditLogService.getAuditLogByEventId(eventId));
    }

    /**
     * GET /agrilink/iam/audit-logs/date-range - Get audit logs by date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByDateRange(startDate, endDate));
    }

    /**
     * GET /agrilink/iam/audit-logs/user/{userId}/date-range - Get audit logs for user within date range
     */
    @GetMapping("/user/{userId}/date-range")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByUserAndDateRange(
            @PathVariable Integer userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByUserAndDateRange(userId, startDate, endDate));
    }

    /**
     * GET /agrilink/iam/audit-logs/module/{module}/date-range - Get audit logs for module within date range
     */
    @GetMapping("/module/{module}/date-range")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByModuleAndDateRange(
            @PathVariable String module,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByModuleAndDateRange(module, startDate, endDate));
    }

    /**
     * GET /agrilink/iam/audit-logs/module/{module}/action/{action} - Get audit logs by module and action
     */
    @GetMapping("/module/{module}/action/{action}")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByModuleAndAction(
            @PathVariable String module,
            @PathVariable String action) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByModuleAndAction(module, action));
    }

    /**
     * GET /agrilink/iam/audit-logs/entity-type/{entityType}/action/{action} - Get audit logs by entity type and action
     */
    @GetMapping("/entity-type/{entityType}/action/{action}")
    public ResponseEntity<List<AuditLogDto>> getAuditLogsByEntityTypeAndAction(
            @PathVariable String entityType,
            @PathVariable String action) {
        return ResponseEntity.ok(auditLogService.getAuditLogsByEntityTypeAndAction(entityType, action));
    }
}
