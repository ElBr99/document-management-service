package com.itq.document_management_service.service;

import com.itq.document_management_service.dto.request.DocumentSearchRequest;
import com.itq.document_management_service.dto.request.DocumentStatusHistoryDto;
import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import com.itq.document_management_service.dto.response.DocumentResponseDto;
import com.itq.document_management_service.dto.response.SubmissionResultsDto;
import com.itq.document_management_service.exception.DocumentNotFoundException;
import com.itq.document_management_service.mapper.DocumentMapper;
import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.SubmissionResult;
import com.itq.document_management_service.reference.UserAction;
import com.itq.document_management_service.repository.DocumentRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.itq.document_management_service.reference.SubmissionResult.*;
import static com.itq.document_management_service.repository.spec.DocumentSpecification.from;

@Service
@Slf4j
public class DocumentProcessingService {

    private DocumentRepository documentRepository;
    private DocumentMapper documentMapper;
    private ApplicationEventPublisher applicationEventPublisher;
    private List<DocumentStatusTransferring> handlers;
    private Map<UserAction, DocumentStatusTransferring> documentStatusProcessingHandlers;


    public DocumentProcessingService(DocumentRepository documentRepository, DocumentMapper documentMapper, ApplicationEventPublisher applicationEventPublisher, List<DocumentStatusTransferring> handlers) {
        this.documentRepository = documentRepository;
        this.documentMapper = documentMapper;
        this.applicationEventPublisher = applicationEventPublisher;
        this.handlers = handlers;

        this.documentStatusProcessingHandlers = handlers.stream()
                .collect(Collectors.toMap(DocumentStatusTransferring::getAction, Function.identity()));
    }


    @Transactional(readOnly = true)
    public DocumentResponseDto getDocument(Long id) {
        var foundDoc = documentRepository.findByDocId(id)
                .orElseThrow(() -> new DocumentNotFoundException("Документ по id: " + id + " не найден"));

        log.info("Документ по id: {} успешно найден", id);
        return documentMapper.mapWithHistoryFromDocument(foundDoc);
    }


    @Transactional(readOnly = true)
    public Page<DocumentResponseDto> getDocuments(List<Long> ids, Pageable pageable) {
        log.info("Получение документов: ids.size={}, page={}, size={}",
                ids.size(), pageable.getPageNumber(), pageable.getPageSize());

        var pagedDocuments = documentRepository.findAllByIdIn(ids, pageable);

        log.info("Документы в кол-ве {} найдены в репозитории", pagedDocuments.getTotalElements());
        return pagedDocuments.map(document -> documentMapper.mapWithoutHistoryFromDoc(document));
    }


    @Transactional
    public void createDocument(CreateDocumentMetadataDto createDocumentMetadataDto) {
        log.info("Происходит создание документа");

        Document createdDocument = documentMapper.mapToDocument(createDocumentMetadataDto);
        createAndPublishEvent(createdDocument, createDocumentMetadataDto.getCreatedBy(), UserAction.CREATE);

        documentRepository.save(createdDocument);
        log.info("Документ успешно создан");
    }

    public List<SubmissionResultsDto> processDocuments(UserAction userAction, List<Long> documentIds, UUID updatedBy) {

        List<Document> foundDocuments = documentRepository.findAllById(documentIds);
        Map<Long, Document> foundMap = foundDocuments
                .stream()
                .collect(Collectors.toMap(Document::getId, doc -> doc));

        return documentIds
                .stream()
                .map(id -> {
                    Document document = foundMap.get(id);
                    if (document == null) {
                        return buildSubmissionResultDto(id, NOT_FOUND);
                    }
                    return documentStatusProcessingHandlers.get(userAction).processDocumentStatusTransferring(document, updatedBy);
                })
                .toList();
    }

    private SubmissionResultsDto buildSubmissionResultDto(Long id, SubmissionResult submissionResult) {
        return SubmissionResultsDto.builder()
                .documentId(id)
                .resultMessage(submissionResult)
                .build();
    }


    private DocumentStatusHistoryDto buildFromDocument(Document document, UUID updatedBy, UserAction userAction) {
        return DocumentStatusHistoryDto.builder()
                .document(document)
                .updatedBy(updatedBy)
                .action(userAction)
                .build();
    }


    private void createAndPublishEvent(Document document, UUID updatedBy, UserAction userAction) {
        var changedStatusDto = buildFromDocument(document, updatedBy, userAction);
        applicationEventPublisher.publishEvent(changedStatusDto);
    }


    public Page<DocumentResponseDto> findByFilter(DocumentSearchRequest req, Pageable pageable) {
        return documentRepository.findAll(from(req), pageable)
                .map(documentMapper::mapWithoutHistoryFromDoc);
    }
}
