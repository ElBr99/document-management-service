package com.itq.document_management_service.controller;

import com.itq.document_management_service.dto.request.ConcurrencyTestApiDto;
import com.itq.document_management_service.dto.response.DocumentResponseDto;
import com.itq.document_management_service.dto.response.SubmissionResultsDto;
import com.itq.document_management_service.reference.SubmissionResult;
import com.itq.document_management_service.service.DocumentProcessingService;
import com.itq.document_management_service.utils.ApiAnswerConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.itq.document_management_service.reference.UserAction.APPROVE;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@Tag(name = "Test API", description = "API для тестирования")
public class ConcurrentApprovementTestApi {

    private final DocumentProcessingService documentProcessingService;

    @PostMapping("/concurrent-approvement")
    public ResponseEntity<Map<String, Object>> concurrentApprove(
            @Valid @RequestBody ConcurrencyTestApiDto testApiDto,
            @Valid @RequestParam @NotNull(message = ApiAnswerConstants.MISSING_VALUE + "Поле инициатора изменения статуса не должно быть пустым") UUID updatedBy) throws InterruptedException {


        AtomicInteger successCounter = new AtomicInteger(0);
        AtomicInteger conflictCounter = new AtomicInteger(0);
        AtomicInteger errorCounter = new AtomicInteger(0);

        ExecutorService executorService = Executors.newFixedThreadPool(testApiDto.getThreadAmount());

        executorService.submit(() ->
                {
                    for (int i = 0; i < testApiDto.getAttempts(); i++) {
                        try {
                            var result = documentProcessingService.processDocuments(APPROVE, testApiDto.getDocIds(), updatedBy);
                            for (SubmissionResultsDto resultsDto : result) {
                                incrementCounters(resultsDto.getResultMessage(), successCounter, conflictCounter, errorCounter);
                            }
                        } catch (Exception exception) {
                            log.error("Попытка {} завершилась ошибкой {}", i, exception.getMessage());
                            errorCounter.incrementAndGet();

                        }
                    }
                }
        );

        executorService.shutdown();

        Map<Long, Object> status = testApiDto.getDocIds().stream()
                .map(documentProcessingService::getDocument)
                .collect(Collectors.toMap(DocumentResponseDto::getId, DocumentResponseDto::getStatus));


        Map<String, Object> response = new HashMap<>();

        response.put("documentIds", testApiDto.getDocIds());
        response.put("totalAttempts", testApiDto.getAttempts());
        response.put("success", successCounter.get());
        response.put("conflict", conflictCounter.get());
        response.put("error", errorCounter.get());
        response.put("finalStatus", status);


        return ResponseEntity.ok(response);
    }

    private void incrementCounters(SubmissionResult submissionResult, AtomicInteger successCounter, AtomicInteger conflictCounter, AtomicInteger errorCounter) {

        switch (submissionResult) {
            case SUCCESS -> successCounter.incrementAndGet();
            case CONFLICT_STATUS -> conflictCounter.incrementAndGet();
            default -> errorCounter.incrementAndGet();
        }

    }
}
