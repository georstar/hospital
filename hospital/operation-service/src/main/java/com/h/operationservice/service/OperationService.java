package com.h.operationservice.service;

import com.h.operationservice.dto.OperationRequestDTO;
import com.h.operationservice.dto.OperationResponseDTO;
import com.h.operationservice.exception.OperationTypeForPatientNotFoundToUndone;
import com.h.operationservice.exception.RequestAllreadyCompleted;
import com.h.operationservice.mapper.OperationMapper;
import com.h.operationservice.model.OperationRecord;
import com.h.operationservice.model.OperationStatus;
import com.h.operationservice.model.OperationType;
import com.h.operationservice.repository.OperationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class OperationService {
    private OperationRepository operationRepository;

    public OperationService(OperationRepository operationRepository) {
        this.operationRepository = operationRepository;
    }

    public List<OperationResponseDTO> getOperations() {
        List<OperationRecord> operations = operationRepository.findAll();

        return operations.stream()
                .map(OperationMapper::toDTO).toList();
    }

    public OperationResponseDTO doOperation(OperationRequestDTO operationRequestDTO) {
        OperationRecord operation;
        //phase1 check if key exists and already completed or received, if not create new
        if (operationRepository.existsByIdempotencyKeyAndStatus(operationRequestDTO.getIdempotencyKey(), OperationStatus.COMPLETED)) {
            throw new RequestAllreadyCompleted("Request with idempotency key " +
                    operationRequestDTO.getIdempotencyKey() + " already completed");
        } else if (operationRepository.existsByIdempotencyKeyAndStatus(operationRequestDTO.getIdempotencyKey(),
                OperationStatus.RECEIVED)) {
            operation = operationRepository.findByIdempotencyKey(
                    operationRequestDTO.getIdempotencyKey()).get();
        } else {
            operation = operationRepository.save(
                    OperationMapper.toModel(operationRequestDTO, OperationStatus.RECEIVED)
            );
        }

        //phase2 perform operation
        operation.setIdempotencyKey(operationRequestDTO.getIdempotencyKey());
        operation.setStatus(OperationStatus.COMPLETED);
        operation.setCompletedAt(Instant.now());
        operation = operationRepository.save(operation);

        return OperationMapper.toDTO(operation);
    }

    public OperationResponseDTO undoOperation(OperationRequestDTO operationRequestDTO) {
        OperationRecord operation;
        //phase1 check if key exists and already completed or received, if not create new
        if (operationRepository.existsByIdempotencyKeyAndStatus(operationRequestDTO.getIdempotencyKey(),
                OperationStatus.UNDONE)) {
            throw new RequestAllreadyCompleted("Request with idempotency key " +
                    operationRequestDTO.getIdempotencyKey() + " already undone");
        } else if (operationRepository.existsByIdempotencyKeyAndStatus(operationRequestDTO.getIdempotencyKey(),
                OperationStatus.RECEIVED_UNDO)) {
            operation = operationRepository.findByIdempotencyKey(
                    operationRequestDTO.getIdempotencyKey()).get();
        }
        else if (operationRepository.existsByPatientIdAndOperationType(
                operationRequestDTO.getPatientId(),
                OperationType.valueOf(operationRequestDTO.getOperationType()))) {
            operation = operationRepository.findByPatientIdAndOperationType(
                    operationRequestDTO.getPatientId(),
                    OperationType.valueOf(operationRequestDTO.getOperationType())).get();
        }
        else {
            throw new OperationTypeForPatientNotFoundToUndone("Operation of type " +
                    operationRequestDTO.getOperationType() + " for patient with Id " +
                    operationRequestDTO.getPatientId() + " not found to undone");
        }

        //phase2 perform operation
        operation.setIdempotencyKey(operationRequestDTO.getIdempotencyKey());
        operation.setStatus(OperationStatus.UNDONE);
        operation.setCompletedAt(Instant.now());
        operation = operationRepository.save(operation);

        return OperationMapper.toDTO(operation);
    }
}
