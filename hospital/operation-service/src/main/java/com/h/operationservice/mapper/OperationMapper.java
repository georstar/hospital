package com.h.operationservice.mapper;

import com.h.operationservice.dto.OperationRequestDTO;
import com.h.operationservice.dto.OperationResponseDTO;
import com.h.operationservice.model.OperationRecord;
import com.h.operationservice.model.OperationStatus;
import com.h.operationservice.model.OperationType;

public class OperationMapper {
    public static OperationResponseDTO toDTO(OperationRecord record) {
        OperationResponseDTO dto = new OperationResponseDTO();
        dto.setIdempotencyKey(record.getIdempotencyKey());
        dto.setPatientId(record.getPatientId());
        dto.setOperationType(record.getOperationType().toString());
        dto.setStatus(record.getStatus().toString());
        return dto;
    }

    public static OperationRecord toModel(OperationRequestDTO dto, OperationStatus status) {
        return new OperationRecord(
                dto.getIdempotencyKey(),
                dto.getPatientId(),
                OperationType.valueOf(dto.getOperationType()),
                status
        );
    }
}
