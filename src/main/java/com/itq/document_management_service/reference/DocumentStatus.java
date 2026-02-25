package com.itq.document_management_service.reference;

import java.util.Set;

public enum DocumentStatus {

    DRAFT,
    SUBMITTED,
    APPROVED;

    private Set<DocumentStatus> possibleNext = Set.of();

    static {

        DRAFT.possibleNext = Set.of(SUBMITTED);
        SUBMITTED.possibleNext=Set.of(APPROVED);

    }

    public boolean canTransitionTo (DocumentStatus documentStatusNext) {
        return this.possibleNext.contains(documentStatusNext);
    }

}
