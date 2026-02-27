package com.itq.document_management_service.controller;

import com.itq.document_management_service.dto.AbstractResponseDto;
import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import com.itq.document_management_service.dto.request.ChangeDocumentStatusDto;
import com.itq.document_management_service.dto.request.DocumentSearchRequest;
import com.itq.document_management_service.dto.response.DocumentResponseDto;
import com.itq.document_management_service.dto.response.SubmissionResultsDto;
import com.itq.document_management_service.dto.response.SuccessResponseDto;
import com.itq.document_management_service.reference.UserAction;
import com.itq.document_management_service.service.DocumentProcessingService;
import com.itq.document_management_service.utils.ApiAnswerConstants;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Tag(name = "Document API", description = "API для работы с документами")
public class DocumentController {

    private final DocumentProcessingService documentProcessingService;

    @PostMapping
    public ResponseEntity<AbstractResponseDto> createDocument(@RequestBody @Valid CreateDocumentMetadataDto createDocumentMetadataDto) {

        log.info("Поступил запрос на создание документа");
        documentProcessingService.createDocument(createDocumentMetadataDto);
        log.info("Документ успешно создан в системе");

        return ResponseEntity.ok(new SuccessResponseDto("Документ успешно создан в системе"));
    }

    @PostMapping("/submit/batch")
    public ResponseEntity<List<SubmissionResultsDto>> submitDocuments(@Valid @RequestBody ChangeDocumentStatusDto changeDocumentStatusDto,
                                                                      @Valid @RequestParam @NotNull(message = ApiAnswerConstants.MISSING_VALUE + "Поле инициатора изменения статуса не должно быть пустым") UUID updatedBy) {

        log.info("Список из {} документов отправлен на согласование", changeDocumentStatusDto.getDocumentIds().size());
        var response = documentProcessingService.processDocuments(UserAction.SUBMIT, changeDocumentStatusDto.getDocumentIds(), updatedBy);
        log.info("Запрос на согласование документов успешно обработан");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/approve/batch")
    public ResponseEntity<List<SubmissionResultsDto>> approveDocuments(@Valid @RequestBody ChangeDocumentStatusDto changeDocumentStatusDto,
                                                                       @Valid @RequestParam @NotNull(message = ApiAnswerConstants.MISSING_VALUE + "Поле инициатора изменения статуса не должно быть пустым") UUID updatedBy) {

        log.info("Список из {} документов отправлен на утверждение", changeDocumentStatusDto.getDocumentIds().size());
        var response = documentProcessingService.processDocuments(UserAction.APPROVE, changeDocumentStatusDto.getDocumentIds(), updatedBy);
        log.info("Запрос на утверждение документов успешно обработан");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentResponseDto> getDocument(@PathVariable Long id) {

        log.info("Поступил запрос на поиск документ с id: {}", id);
        var response = documentProcessingService.getDocument(id);
        log.info("Документ с id: {} успешно найден", id);

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<DocumentResponseDto>> getDocuments(@RequestParam List<Long> ids,
                                                                  @PageableDefault(size = 10) Pageable pageable) {

        log.info("Поступил запрос на поиск {} элементов", ids.size());

        Sort sort = Sort.by(
                Sort.Order.desc("createdAt"),
                Sort.Order.asc("title")
        );

        Pageable pageableWithSort = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        var response = documentProcessingService.getDocuments(ids, pageableWithSort);

        log.info("Запрос успешно обработан. Кол-во найденных элементов: {}", response.getTotalElements());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DocumentResponseDto>> search(
            DocumentSearchRequest req,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(documentProcessingService.findByFilter(req, pageable));
    }
}
