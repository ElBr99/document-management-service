package com.itq.document_management_service.service;

import com.itq.document_management_service.config.properties.GenerateDocumentProperties;
import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static java.util.stream.IntStream.range;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentUtilService {

    private final DocumentProcessingService documentProcessingService;
    private final GenerateDocumentProperties properties;

    public void generateDocuments() {
        log.info("Поступил запрос на создание {} документов через утилиту", properties.getBatchSize());

        range(0, properties.getBatchSize()).forEach(iterationNumber -> {
            CreateDocumentMetadataDto createDocumentMetadataDto = new CreateDocumentMetadataDto(
                    properties.getCreatedBy(),
                    properties.getTitle() + UUID.randomUUID()
            );

            try {
                log.info("Создаётся документ с названием {}", createDocumentMetadataDto.getTitle());
                documentProcessingService.createDocument(createDocumentMetadataDto);
                log.info("Документ с названием {} успешно создан", createDocumentMetadataDto.getTitle());
            } catch (Exception e) {
                log.error("Документ с названием {} не может быть создан по причине : {}", createDocumentMetadataDto.getTitle(), e.getMessage());
            }
        });
    }
}
