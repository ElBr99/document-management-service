package com.itq.document_management_service.job;

import static com.itq.document_management_service.reference.DocumentStatus.APPROVED;
import static com.itq.document_management_service.reference.DocumentStatus.SUBMITTED;
import static com.itq.document_management_service.reference.UserAction.*;
import static org.springframework.data.domain.Pageable.ofSize;

import com.itq.document_management_service.config.properties.DocumentJobProperties;
import com.itq.document_management_service.reference.UserAction;
import com.itq.document_management_service.repository.DocumentRepository;
import com.itq.document_management_service.service.DocumentProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

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
                SUBMITTED, ofSize(documentJobProperties.getMoveToSubmittedBatchSize())
        );

        documentProcessingService.processDocuments(SUBMIT, ids, documentJobProperties.getUpdatedBy());
    }


    @Scheduled(cron = "${document.job.move-to-approved.cron}")
    public void moveToApproved() {
        List<Long> ids = documentRepository.findIdsByStatus(
                APPROVED, ofSize(documentJobProperties.getMoveToApprovedBatchSize())
        );

        documentProcessingService.processDocuments(APPROVE, ids, documentJobProperties.getUpdatedBy());
    }
}
