package com.itq.document_management_service.exception;

public class ChangeDocumentStatusConflictException extends RuntimeException {
    public ChangeDocumentStatusConflictException(String message) {
        super(message);
    }
}
