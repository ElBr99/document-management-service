package com.itq.document_management_service.service;


import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.UserAction;

import java.util.UUID;

public interface DocumentStatusTransferring {

    UserAction getAction();

    Document processDocumentStatusTransferring(Document foundDocument, UUID updatedBy);

}
