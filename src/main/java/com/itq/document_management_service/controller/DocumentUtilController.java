package com.itq.document_management_service.controller;

import com.itq.document_management_service.dto.AbstractResponseDto;
import com.itq.document_management_service.dto.response.SuccessResponseDto;
import com.itq.document_management_service.service.DocumentUtilService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/utils")
@RequiredArgsConstructor
@Tag(name = "Util API", description = "API для работы с документами")
public class DocumentUtilController {

    private final DocumentUtilService documentUtilService;

    @PostMapping("/generate-documents")
    public ResponseEntity<AbstractResponseDto> createDocument () {
        log.info("Поступил запрос на генерацию документов");
        documentUtilService.generateDocuments();
        return ResponseEntity.ok(new SuccessResponseDto("Документы успешно созданы в системе"));
    }
}
