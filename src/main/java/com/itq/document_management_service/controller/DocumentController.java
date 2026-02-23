package com.itq.document_management_service.controller;

import com.itq.document_management_service.dto.AbstractResponseDto;
import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import com.itq.document_management_service.dto.request.SubmitDocumentDto;
import com.itq.document_management_service.dto.response.SuccessResponseDto;
import com.itq.document_management_service.service.DocumentProcessingService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Document API", description = "API для работы с документами")
public class DocumentController {

    private final DocumentProcessingService documentProcessingService;

    @PostMapping
    public ResponseEntity<AbstractResponseDto> createDocument (@RequestBody @Valid CreateDocumentMetadataDto createDocumentMetadataDto) {
        log.info("Поступил запрос на создание документа");
        documentProcessingService.createAndSaveDocument(createDocumentMetadataDto);

        log.info("Документ успешно создан в системе");

        return ResponseEntity.ok(new SuccessResponseDto("Документ успешно создан в системе"));
    }

    @PatchMapping
    public ResponseEntity <AbstractResponseDto> submitDocument (@Valid @RequestBody SubmitDocumentDto submitDocumentDto) {
        log.info ("Список из " + submitDocumentDto.getDocumentIds().size() + " документов отправлен на согласование");

        documentProcessingService.sendDocumentsToSubmission(submitDocumentDto.getDocumentIds());


    }


}
