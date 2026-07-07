# Audit Logging Implementation Guide

## Overview

This guide explains how to implement comprehensive audit logging across all AgriLink microservices. The audit logging system tracks all CRUD operations (Create, Read, Update, Delete) and captures:

- **User Information**: Who performed the action
- **Timestamp**: When the action occurred
- **Module**: Which service performed the action
- **Action**: What operation was performed (CREATE, UPDATE, DELETE)
- **Entity**: What was being acted upon
- **Before/After States**: Data before and after the operation
- **Request Details**: IP address, session ID, etc.
- **Event ID**: Unique identifier for tracking

## Architecture

### Core Components

1. **AuditLog Entity** (`AuditLog.java`)
   - JPA entity representing an audit log record in the database
   - Stores all audit information in the service's database

2. **AuditLogRepository** (`AuditLogRepository.java`)
   - Spring Data JPA repository for CRUD operations on audit logs
   - Provides query methods for filtering by user, module, action, date range, etc.

3. **AuditLogService** (`AuditLogService.java`)
   - Business logic layer for audit operations
   - Generates unique event IDs
   - Converts entities to DTOs
   - Calculates changes between states

4. **AuditLogController** (`AuditLogController.java`)
   - REST endpoints for retrieving audit logs
   - Supports filtering by various criteria

5. **@Audit Annotation** (`Audit.java`)
   - Custom annotation to mark methods for automatic audit logging
   - Declarative approach - less boilerplate code

6. **AuditAspect** (`AuditAspect.java`)
   - AOP aspect that intercepts methods annotated with @Audit
   - Extracts user info, IP address, session ID
   - Captures method arguments and results
   - Logs errors if operations fail

7. **AuditInterceptor** (`AuditInterceptor.java`)
   - Spring interceptor for request/response tracking
   - Logs request timing and HTTP details

## Database Schema

The AuditLog table is automatically created with the following structure:

```sql
CREATE TABLE AuditLog (
  AuditLogID INT PRIMARY KEY AUTO_INCREMENT,
  UserID INT NOT NULL,
  SessionID VARCHAR(100),
  Module VARCHAR(50) NOT NULL,
  Action VARCHAR(20) NOT NULL,
  EntityType VARCHAR(100) NOT NULL,
  EntityID VARCHAR(50),
  Before JSON,
  After JSON,
  Changes JSON,
  EventID VARCHAR(50) UNIQUE NOT NULL,
  IPAddress VARCHAR(100),
  Timestamp DATETIME NOT NULL,
  Description TEXT,
  Status VARCHAR(20),
  ErrorMessage TEXT
);
```

## Usage Guide

### 1. Add Audit Annotation to Service Methods

```java
@Service
public class UserService {
    
    @Audit(module = "IDENTITY", action = "CREATE", entityType = "user_details", captureResult = "true")
    public UserResponseDto createUser(CreateUserRequestDto dto, UserDetails currentUser) {
        // Implementation
    }
    
    @Audit(module = "IDENTITY", action = "UPDATE", entityType = "user_details", entityIdIndex = "0")
    public UserResponseDto updateUser(Integer userId, UpdateUserRequestDto dto) {
        // Implementation
    }
    
    @Audit(module = "IDENTITY", action = "DELETE", entityType = "user_details", entityIdIndex = "0")
    public void deleteUser(Integer userId) {
        // Implementation
    }
}
```

### 2. Audit Annotation Parameters

- **module**: The module/service name (e.g., "IDENTITY", "FARMER", "CROP")
- **action**: The operation type ("CREATE", "UPDATE", "DELETE", "READ", "LOGIN", "LOGOUT")
- **entityType**: The entity being operated on (e.g., "user_details", "farmer_profile")
- **entityIdIndex**: Index of entity ID in method parameters (0-based, optional)
- **beforeStateIndex**: Index of before-state object in parameters (optional, for updates)
- **captureResult**: Whether to capture method result as after-state ("true" or "false")

### 3. REST API Endpoints for Audit Logs

#### Get All Audit Logs
```bash
GET /agrilink/iam/audit-logs
```

#### Get Audit Log by ID
```bash
GET /agrilink/iam/audit-logs/{id}
```

#### Get Audit Logs by User
```bash
GET /agrilink/iam/audit-logs/user/{userId}
```

#### Get Audit Logs by Module
```bash
GET /agrilink/iam/audit-logs/module/{module}
```

#### Get Audit Logs by Action
```bash
GET /agrilink/iam/audit-logs/action/{action}
```

#### Get Audit Logs by Entity Type
```bash
GET /agrilink/iam/audit-logs/entity-type/{entityType}
```

#### Get Audit Logs by Entity ID
```bash
GET /agrilink/iam/audit-logs/entity/{entityId}
```

#### Get Audit Log by Event ID
```bash
GET /agrilink/iam/audit-logs/event/{eventId}
```

#### Get Audit Logs by Date Range
```bash
GET /agrilink/iam/audit-logs/date-range?startDate=2026-06-22T00:00:00&endDate=2026-06-23T23:59:59
```

#### Get User Audit Logs by Date Range
```bash
GET /agrilink/iam/audit-logs/user/{userId}/date-range?startDate=2026-06-22T00:00:00&endDate=2026-06-23T23:59:59
```

#### Get Module Audit Logs by Date Range
```bash
GET /agrilink/iam/audit-logs/module/{module}/date-range?startDate=2026-06-22T00:00:00&endDate=2026-06-23T23:59:59
```

#### Get Audit Logs by Module and Action
```bash
GET /agrilink/iam/audit-logs/module/{module}/action/{action}
```

## Audit Log Entry Example

```json
{
  "auditLogId": 1,
  "userId": 1,
  "sessionId": "550e8400-e29b-41d4-a716-446655440000",
  "module": "IDENTITY",
  "action": "CREATE",
  "entityType": "user_details",
  "entityId": "5",
  "before": "{}",
  "after": "{\"id\":5,\"name\":\"Asha\",\"email\":\"asha@example.com\",\"status\":\"A\"}",
  "changes": "{\"id\":5,\"name\":\"Asha\",\"email\":\"asha@example.com\",\"status\":\"A\"}",
  "eventId": "evt-20260622-A1B2C3D4",
  "ipAddress": "192.168.1.100",
  "timestamp": "2026-06-22T07:00:00",
  "description": "IDENTITY - CREATE operation on user_details (Duration: 145ms)",
  "status": "SUCCESS",
  "errorMessage": null
}
```

## Implementation Steps for Other Microservices

To add audit logging to other microservices (farmer, crop, input, subsidy, produce, report, notification):

1. **Copy audit package** from iam-service to target service:
   - `/audit/entity/AuditLog.java`
   - `/audit/repository/AuditLogRepository.java`
   - `/audit/dto/AuditLogDto.java`
   - `/audit/service/AuditLogService.java`
   - `/audit/controller/AuditLogController.java`
   - `/audit/aspect/AuditAspect.java`
   - `/audit/annotation/Audit.java`
   - `/audit/config/AuditConfig.java`
   - `/audit/config/WebConfig.java`
   - `/audit/interceptor/AuditInterceptor.java`

2. **Update module paths** in copied files to match target service package structure

3. **Add AOP dependency** to service's pom.xml (if not present)

4. **Annotate service methods** with @Audit annotation

5. **Update properties file** with module-specific configuration (if needed)

6. **Test audit logging** with curl commands

## Configuration

Add to `application.properties`:

```properties
# Audit Logging Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Enable AOP
spring.aop.proxy-target-class=true

# Logging
logging.level.com.cognizant.agrilink.iam.audit=DEBUG
```

## Security Considerations

1. **User Identification**: Audit logs capture the authenticated user performing the action
2. **Tamper Prevention**: Store audit logs in dedicated table with unique event IDs
3. **Access Control**: Restrict audit log retrieval to authorized users (Admin/Auditor roles)
4. **Data Retention**: Implement retention policies to manage database storage
5. **Sensitive Data**: Consider encrypting sensitive data in before/after states

## Testing

### Test Creating a User with Audit Logging

```bash
curl -X POST http://localhost:8081/agrilink/iam/users \
  -H "Content-Type: application/json" \
  -d '{
    "name":"John Doe",
    "email":"john@example.com",
    "phone":"9999999999",
    "roleId":1,
    "regionId":1,
    "passwordHash":"hashedpassword",
    "status":"Active"
  }'
```

### Retrieve Audit Logs

```bash
# Get all audit logs
curl http://localhost:8081/agrilink/iam/audit-logs

# Get audit logs for a user
curl http://localhost:8081/agrilink/iam/audit-logs/user/1

# Get audit logs by module
curl http://localhost:8081/agrilink/iam/audit-logs/module/IDENTITY

# Get audit logs by action
curl http://localhost:8081/agrilink/iam/audit-logs/action/CREATE
```

## Performance Optimization

1. **Async Logging**: Consider using @Async for audit logging to prevent blocking
2. **Batch Operations**: Implement batch inserts for high-volume logging
3. **Archival**: Archive old audit logs to separate tables/databases
4. **Indexing**: Add database indexes on frequently queried columns (UserID, Module, Action, Timestamp)

## Troubleshooting

### Issue: Audit logs not being created
- Check if @Audit annotation is present on service methods
- Verify AuditAspect is being loaded (check logs for "Audit Interceptor" messages)
- Ensure AOP is enabled: `spring.aop.proxy-target-class=true`

### Issue: Cannot get user from security context
- Ensure authentication is properly configured
- Check Security Context is populated before method execution
- Review UserDetails implementation in service

### Issue: Database table not created
- Verify `spring.jpa.hibernate.ddl-auto=update` is set
- Check database user has permissions to create tables
- Review Hibernate logs for DDL execution

## References

- [Spring AOP Documentation](https://spring.io/guides/gs/aspect-oriented/)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Custom Annotations in Java](https://www.baeldung.com/java-custom-annotation)
