package com.itq.document_management_service.listener;

import com.itq.document_management_service.dto.request.DocumentRegistryDto;
import com.itq.document_management_service.exception.DocumentRegistrySavingException;
import com.itq.document_management_service.mapper.DocumentRegistryMapper;
import com.itq.document_management_service.repository.DocumentRegistryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.event.TransactionPhase.BEFORE_COMMIT;

@Component
@RequiredArgsConstructor
@Slf4j
public class DocumentRegistrySavingListener {
    private final DocumentRegistryRepository documentRegistryRepository;
    private final DocumentRegistryMapper  documentRegistryMapper;

    @TransactionalEventListener(phase = BEFORE_COMMIT)
    public void addApprovedDocumentToRegistry(DocumentRegistryDto documentRegistryDto) {
        log.info("Осуществляется сохранение информации в реестр утверждений по документу с documentNumber " + documentRegistryDto.getDocument().getDocumentNumber());

        var docForSaving = documentRegistryMapper.mapToDocumentRegistry(documentRegistryDto);

        try {
            documentRegistryRepository.save(docForSaving);
            log.info("Документ с documentNumber " + docForSaving.getDocument().getDocumentNumber() + " успешно сохранен в реестре утверждений");
        } catch (Exception e) {
            log.error("Документ с documentNumber " + docForSaving.getDocument().getDocumentNumber() + " не может быть сохранен в реестр утверждений");
            throw new DocumentRegistrySavingException("Ошибка при сохранении документа в реестр утверждений");
        }

    }

}
