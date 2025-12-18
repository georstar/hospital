package com.h.operationservice.repository;

import com.h.operationservice.model.OperationRecord;
import com.h.operationservice.model.OperationStatus;
import com.h.operationservice.model.OperationType;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OperationRepository extends JpaRepository<OperationRecord, UUID> {

    Optional<OperationRecord> findByIdempotencyKey(String key);
    boolean existsByIdempotencyKeyAndStatus(String idempotencyKey, OperationStatus status);
    Optional<OperationRecord> findByPatientIdAndOperationType(String patientId, OperationType operationType);
    boolean existsByPatientIdAndOperationType(String patientId, OperationType operationType);
}
