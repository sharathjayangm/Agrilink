package com.cognizant.agrilink.iam.audit.repository;

import com.cognizant.agrilink.iam.audit.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    /**
     * Find all audit logs for a specific user
     */
    List<AuditLog> findByUserId(Integer userId);

    /**
     * Find all audit logs for a specific module
     */
    List<AuditLog> findByModule(String module);

    /**
     * Find all audit logs for a specific action
     */
    List<AuditLog> findByAction(String action);

    /**
     * Find audit logs by entity type
     */
    List<AuditLog> findByEntityType(String entityType);

    /**
     * Find audit logs by entity ID
     */
    List<AuditLog> findByEntityId(String entityId);

    /**
     * Find audit log by unique event ID
     */
    Optional<AuditLog> findByEventId(String eventId);

    /**
     * Find audit logs within a date range
     */
    @Query("SELECT a FROM AuditLog a WHERE a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AuditLog> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find audit logs for a user within a date range
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userId = :userId AND a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AuditLog> findByUserIdAndDateRange(@Param("userId") Integer userId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find audit logs for a module within a date range
     */
    @Query("SELECT a FROM AuditLog a WHERE a.module = :module AND a.timestamp BETWEEN :startDate AND :endDate ORDER BY a.timestamp DESC")
    List<AuditLog> findByModuleAndDateRange(@Param("module") String module, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);

    /**
     * Find audit logs for a module and action
     */
    @Query("SELECT a FROM AuditLog a WHERE a.module = :module AND a.action = :action ORDER BY a.timestamp DESC")
    List<AuditLog> findByModuleAndAction(@Param("module") String module, @Param("action") String action);

    /**
     * Find audit logs by entity and action
     */
    @Query("SELECT a FROM AuditLog a WHERE a.entityType = :entityType AND a.action = :action ORDER BY a.timestamp DESC")
    List<AuditLog> findByEntityTypeAndAction(@Param("entityType") String entityType, @Param("action") String action);
}
