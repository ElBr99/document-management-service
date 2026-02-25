package com.itq.document_management_service.dto.request;

import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.DocumentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRegistryDto {

    private Long id;
    private Document document;
    private DocumentStatus status;
    private UUID registeredBy;
    private LocalDateTime registeredAt;
    private String comment;

}
