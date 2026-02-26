package com.itq.document_management_service.reference;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Data;


public enum SubmissionResult {
    SUCCESS("Успешно"),
    CONFLICT_STATUS("Конфликт статусов документа"),
    NOT_FOUND("Не найдено"),
    UPDATING_ERROR("Ошибка при обновлении"),
    DOCUMENT_REGISTRY_ERROR ("Ошибка регистрации в реестре");


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
