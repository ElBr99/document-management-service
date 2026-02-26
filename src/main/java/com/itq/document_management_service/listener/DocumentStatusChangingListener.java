package com.itq.document_management_service.listener;

import com.itq.document_management_service.dto.request.DocumentStatusHistoryDto;
import com.itq.document_management_service.mapper.DocumentHistoryMapper;
import com.itq.document_management_service.repository.DocumentHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentStatusChangingListener {

    private final DocumentHistoryRepository documentHistoryRepository;
    private final DocumentHistoryMapper documentHistoryMapper;

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void addChangedDocumentStatusToHistory(DocumentStatusHistoryDto changeDocumentStatusDto) {
        log.info("Осуществляется сохранение истории по смене статуса по документу с documentNumber {}", changeDocumentStatusDto.getDocument());
        var docHistoryForSaving = documentHistoryMapper.mapToDocument(changeDocumentStatusDto);
        documentHistoryRepository.save(docHistoryForSaving);
        log.info("История по смене статуса документа с documentNumber {} успешно сохранена", docHistoryForSaving.getDocument());
    }
}
