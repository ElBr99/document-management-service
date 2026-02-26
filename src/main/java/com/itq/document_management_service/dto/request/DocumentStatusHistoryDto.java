package com.itq.document_management_service.dto.request;

import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.UserAction;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentStatusHistoryDto {

    private Long id;
    private Document document;
    private UUID updatedBy;
    private LocalDateTime updatedAt;
    private UserAction action;
    private String comment;

}
