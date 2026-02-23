package com.itq.document_management_service.service;

import com.itq.document_management_service.dto.AbstractResponseDto;
import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import com.itq.document_management_service.dto.response.SubmissionResultsDto;
import com.itq.document_management_service.dto.response.SuccessResponseDto;
import com.itq.document_management_service.mapper.DocumentMapper;
import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.SubmissionResult;
import com.itq.document_management_service.repository.DocumentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DocumentProcessingService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;

    @Transactional
    public void createAndSaveDocument(CreateDocumentMetadataDto createDocumentMetadataDto) {

        log.info("Происходит создание документа");

        Document createdDocument = documentMapper.mapToDocument(createDocumentMetadataDto);
        documentRepository.save(createdDocument);

        log.info("Документ успешно создан");
    }

    public SubmissionResultsDto sendDocumentsToSubmission(List<Long> documentIds) {

        List<Document> foundDocuments = documentRepository.findAllById(documentIds);

        Map<Long, Document> foundMap = foundDocuments.stream()
                .collect(Collectors.toMap(Document::getId, doc -> doc));

        processNotFoundDocuments(documentIds, foundMap);




    }

    private List<Long> checkNotFoundDocuments(List<Long> documentIds, Map<Long, Document> foundMap) {
        return documentIds.stream()
                .filter(id -> !foundMap.containsKey(id))
                .toList();
    }

    private SubmissionResultsDto buildSubmissionResultDto(List<Long> ids, SubmissionResult submissionResult) {
        List<SubmissionResultsDto.Results> results = new ArrayList<>();
        ids.forEach(id -> {

            SubmissionResultsDto.Results result = SubmissionResultsDto.Results.builder()
                    .documentId(id)
                    .resultMessage(submissionResult)
                    .build();

            results.add(result);
        });

        return new SubmissionResultsDto(results);
    }

    private SubmissionResultsDto processNotFoundDocuments(List<Long> documentIds, Map<Long, Document> foundMap) {
        var notFoundDocumentsIds = checkNotFoundDocuments(documentIds, foundMap);
        return buildSubmissionResultDto(notFoundDocumentsIds, SubmissionResult.NOT_FOUND);
    }


}
