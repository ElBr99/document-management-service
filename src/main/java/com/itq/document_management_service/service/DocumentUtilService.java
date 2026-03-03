package com.itq.document_management_service.service;

import com.itq.document_management_service.aspect.LogDocumentProceeding;
import com.itq.document_management_service.config.properties.GenerateDocumentProperties;
import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.stream.IntStream.range;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentUtilService {

    private final DocumentProcessingService documentProcessingService;
    private final GenerateDocumentProperties properties;

    @LogDocumentProceeding
    public void generateDocuments() {
        log.info("Обработка запроса на генерацию {} документов через утилиту", properties.getBatchSize());

        AtomicInteger createdCount = new AtomicInteger();

        range(0, properties.getBatchSize()).forEach(iterationNumber -> {

            CreateDocumentMetadataDto createDocumentMetadataDto = new CreateDocumentMetadataDto(
                    properties.getCreatedBy(),
                    properties.getTitle() + UUID.randomUUID()
            );

            try {
                log.info("Создаётся документ с названием {}", createDocumentMetadataDto.getTitle());
                documentProcessingService.createDocument(createDocumentMetadataDto);
                createdCount.getAndIncrement();

                log.info("Документ с названием {} успешно создан", createDocumentMetadataDto.getTitle());
                logProgress(properties.getBatchSize(), iterationNumber);
            } catch (Exception e) {
                log.error("Документ с названием {} не может быть создан по причине : {}", createDocumentMetadataDto.getTitle(), e.getMessage());
            }
        });
        log.info("Обработка запроса на генерацию {} документов через утилиту завершена, успешно создано {}",
                properties.getBatchSize(), createdCount);
    }

    private void logProgress(Integer batchSize, int iterationNumber) {
        if (iterationNumber == batchSize - 1 || (iterationNumber + 1) % (batchSize / 10) == 0) {
            int percent = (int) ((iterationNumber + 1) * 100.0 / batchSize);
            log.info("Создано {} из {} документов ({}%)", iterationNumber + 1, batchSize, percent);
        }
    }
}
