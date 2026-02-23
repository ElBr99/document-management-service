package com.itq.document_management_service.reference;

import com.fasterxml.jackson.annotation.JsonValue;

public enum SubmissionResult {
    SUCCESS("Успешно"),
    CONFLICT("Конфликт"),
    NOT_FOUND("Не успешно");

    private final String resultName;

    SubmissionResult(String resultName) {
        this.resultName = resultName;
    }

    @Override
    @JsonValue
    public String toString() {
        return resultName;
    }
}
