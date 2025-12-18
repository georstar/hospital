package com.h.operationservice.controller;

import com.h.operationservice.dto.OperationRequestDTO;
import com.h.operationservice.dto.OperationResponseDTO;
import com.h.operationservice.service.OperationService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/operations")
public class OperationController {
    private final OperationService operationService;
    public OperationController(OperationService operationService) {
        this.operationService = operationService;
    }

    @GetMapping
    public ResponseEntity<List<OperationResponseDTO>> getOperations(){
        return ResponseEntity.ok().body(operationService.getOperations());
    }

    @PostMapping("/{patientId}/do")
    public ResponseEntity<OperationResponseDTO> doOperation(@PathVariable String patientId, @RequestHeader("Idempotency-Key") String idempotencyKey, @Valid @RequestBody OperationRequestDTO operationRequestDTO){
        operationRequestDTO.setIdempotencyKey(idempotencyKey);
        operationRequestDTO.setPatientId(patientId);
        OperationResponseDTO operationResponseDTO = operationService.doOperation(operationRequestDTO);
        return ResponseEntity.ok().body(operationResponseDTO);
    }

    @PostMapping("/{patientId}/undo")
    public ResponseEntity<OperationResponseDTO> undoOperation(@PathVariable String patientId, @RequestHeader("Idempotency-Key") String idempotencyKey ,
                                                           @Valid @RequestBody OperationRequestDTO operationRequestDTO){
        operationRequestDTO.setIdempotencyKey(idempotencyKey);
        operationRequestDTO.setPatientId(patientId);
        OperationResponseDTO operationResponseDTO = operationService.undoOperation(operationRequestDTO);
        return ResponseEntity.ok().body(operationResponseDTO);
    }
}
