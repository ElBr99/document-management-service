package com.itq.document_management_service.job;

import com.itq.document_management_service.config.properties.DocumentJobProperties;
import com.itq.document_management_service.repository.DocumentRepository;
import com.itq.document_management_service.service.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.itq.document_management_service.reference.DocumentStatus.DRAFT;
import static com.itq.document_management_service.reference.DocumentStatus.SUBMITTED;
import static com.itq.document_management_service.reference.UserAction.APPROVE;
import static com.itq.document_management_service.reference.UserAction.SUBMIT;
import static org.springframework.data.domain.Pageable.ofSize;

@Slf4j
@Component
@RequiredArgsConstructor
public class DocumentProcessingJob {

    private final DocumentProcessingService documentProcessingService;
    private final DocumentRepository documentRepository;
    private final DocumentJobProperties documentJobProperties;

    @Scheduled(cron = "${document.job.move-to-submitted.cron}")
    public void moveToSubmitted() {
        List<Long> ids = documentRepository.findIdsByStatus(
                DRAFT, ofSize(documentJobProperties.getMoveToSubmittedBatchSize())
        );

        documentProcessingService.processDocuments(SUBMIT, ids, documentJobProperties.getUpdatedBy());
    }


    @Scheduled(cron = "${document.job.move-to-approved.cron}")
    public void moveToApproved() {
        List<Long> ids = documentRepository.findIdsByStatus(
                SUBMITTED, ofSize(documentJobProperties.getMoveToApprovedBatchSize())
        );

        documentProcessingService.processDocuments(APPROVE, ids, documentJobProperties.getUpdatedBy());
    }
}
