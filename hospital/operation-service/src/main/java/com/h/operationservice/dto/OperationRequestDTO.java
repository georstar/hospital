package com.h.operationservice.dto;

import jakarta.validation.constraints.NotBlank;

public class OperationRequestDTO {
    private String idempotencyKey;
    private String patientId;
    @NotBlank(message = "Operation Type is required")
    private String operationType;

    public String getIdempotencyKey() {
        return idempotencyKey;
    }

    public void setIdempotencyKey(String idempotencyKey) {
        this.idempotencyKey = idempotencyKey;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
