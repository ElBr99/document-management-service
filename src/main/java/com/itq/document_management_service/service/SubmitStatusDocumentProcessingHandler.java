package com.itq.document_management_service.service;

import com.itq.document_management_service.dto.request.DocumentStatusHistoryDto;
import com.itq.document_management_service.dto.response.SubmissionResultsDto;
import com.itq.document_management_service.exception.ChangeDocumentStatusConflictException;
import com.itq.document_management_service.exception.DocumentNotFoundException;
import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.DocumentStatus;
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

import static com.itq.document_management_service.reference.DocumentStatus.SUBMITTED;
import static com.itq.document_management_service.reference.SubmissionResult.*;
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
    public SubmissionResultsDto processDocumentStatusTransferring(Document foundDocument, UUID updatedBy) {
        try {
            ChangeDocumentStatusValidator.validateStatus(foundDocument.getStatus(), SUBMITTED);
            var updatedDoc = documentRepository.updateStatusById(foundDocument.getId(), DocumentStatus.DRAFT.name(), SUBMITTED.name());

            if (updatedDoc == null) {
                throw new DocumentNotFoundException("Документ с таким id не найден");
            }

            createAndPublishEvent(updatedDoc, updatedBy, SUBMIT);
            return buildSubmissionResultDto(foundDocument.getId(), SUCCESS);
        } catch (DocumentNotFoundException e) {
            return buildSubmissionResultDto(foundDocument.getId(), NOT_FOUND);
        } catch (ChangeDocumentStatusConflictException exception) {
            return buildSubmissionResultDto(foundDocument.getId(), CONFLICT_STATUS);
        } catch (Exception e) {
            return buildSubmissionResultDto(foundDocument.getId(), UPDATING_ERROR);
        }
    }


    private DocumentStatusHistoryDto buildFromDocument(Document document, UUID updatedBy, UserAction userAction) {
        return DocumentStatusHistoryDto.builder()
                //.id(document.getId())
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
