package com.itq.document_management_service.service;

import static java.util.stream.IntStream.*;

import com.itq.document_management_service.config.properties.GenerateDocumentProperties;
import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentUtilService {

    private final DocumentProcessingService documentProcessingService;
    private final GenerateDocumentProperties properties;

    public void generateDocuments() {
        range(0, properties.getBatchSize()).forEach(iterationNumber -> {
            CreateDocumentMetadataDto createDocumentMetadataDto = new CreateDocumentMetadataDto(
                    properties.getCreatedBy(),
                    properties.getTitle() + UUID.randomUUID()
            );

            documentProcessingService.createDocument(createDocumentMetadataDto);
        });
    }
}
