package com.itq.document_management_service.service;

import com.itq.document_management_service.dto.request.DocumentStatusHistoryDto;
import com.itq.document_management_service.dto.response.SubmissionResultsDto;
import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.SubmissionResult;
import com.itq.document_management_service.reference.UserAction;
import com.itq.document_management_service.repository.DocumentRepository;
import com.itq.document_management_service.validator.ChangeDocumentStatusValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.itq.document_management_service.reference.DocumentStatus.DRAFT;
import static com.itq.document_management_service.reference.DocumentStatus.SUBMITTED;
import static com.itq.document_management_service.reference.UserAction.SUBMIT;

@Component
@Slf4j
@RequiredArgsConstructor
public class SubmitStatusDocumentProcessingHandler implements DocumentStatusTransferring {

    private final DocumentRepository documentRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public UserAction getAction() {
        return SUBMIT;
    }


    @Transactional
    @Override
    public Document processDocumentStatusTransferring(Document foundDocument, UUID updatedBy) {
        log.info("Обработка запроса на перевод статуса документа c documentNumber {} из статуса {} в {}", foundDocument.getDocumentNumber(), foundDocument.getStatus().name(), SUBMITTED);
        ChangeDocumentStatusValidator.validateStatus(foundDocument.getStatus(), SUBMITTED);
        var updatedDoc = documentRepository.updateStatusById(foundDocument.getId(), DRAFT.name(), SUBMITTED.name());

        createAndPublishEvent(updatedDoc, updatedBy, SUBMIT);

        log.info("Документ c documentNumber {} успешно переведен в статус {}", foundDocument.getDocumentNumber(), updatedDoc.getStatus().name());
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

    private SubmissionResultsDto buildSubmissionResultDto(Long id, SubmissionResult submissionResult) {

        return SubmissionResultsDto.builder()
                .documentId(id)
                .resultMessage(submissionResult)
                .build();
    }
}
