package com.itq.document_management_service.validator;

import com.itq.document_management_service.exception.ChangeDocumentStatusConflictException;
import com.itq.document_management_service.reference.DocumentStatus;
import com.itq.document_management_service.utils.ApiAnswerConstants;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ChangeDocumentStatusValidator {

    public static void validateStatus (DocumentStatus previous, DocumentStatus changeTo) {
        if (!previous.canTransitionTo(changeTo))  {
            throw new ChangeDocumentStatusConflictException(ApiAnswerConstants.CONFLICT_ERROR + " нельзя изменить статус " + previous.name() + " на статус " + changeTo.name());
        }
    }
}
