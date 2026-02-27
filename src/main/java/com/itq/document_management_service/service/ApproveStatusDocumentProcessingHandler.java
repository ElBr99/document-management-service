package com.itq.document_management_service.service;

import com.itq.document_management_service.dto.request.DocumentStatusHistoryDto;
import com.itq.document_management_service.dto.request.DocumentRegistryDto;
import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.DocumentStatus;
import com.itq.document_management_service.reference.UserAction;
import com.itq.document_management_service.repository.DocumentRepository;
import com.itq.document_management_service.validator.ChangeDocumentStatusValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.itq.document_management_service.reference.DocumentStatus.APPROVED;
import static com.itq.document_management_service.reference.DocumentStatus.SUBMITTED;
import static com.itq.document_management_service.reference.UserAction.APPROVE;

@Component
@Slf4j
@RequiredArgsConstructor
public class ApproveStatusDocumentProcessingHandler implements DocumentStatusTransferring {

    private final DocumentRepository documentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public UserAction getAction() {
        return APPROVE;
    }

    @Transactional
    @Override
    public Document processDocumentStatusTransferring(Document foundDocument, UUID updatedBy) {
            ChangeDocumentStatusValidator.validateStatus(foundDocument.getStatus(), APPROVED);
            var updatedDoc = documentRepository.updateStatusById(foundDocument.getId(), SUBMITTED.name(), APPROVED.name());

            createAndPublishEvent(updatedDoc, updatedBy, APPROVE);
            createAndPublishRegistryEvent(updatedDoc, updatedBy, APPROVED);

            return updatedDoc;
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


    private DocumentRegistryDto buildDocRegistryFromDocument(Document document, UUID registeredBy, DocumentStatus documentStatus) {
        return DocumentRegistryDto.builder()
                .document(document)
                .status(documentStatus)
                .registeredBy(registeredBy)
                .build();
    }

    private void createAndPublishRegistryEvent(Document document, UUID updatedBy, DocumentStatus documentStatus) {
        var docRegistry = buildDocRegistryFromDocument(document, updatedBy, documentStatus);
        applicationEventPublisher.publishEvent(docRegistry);
    }
}
