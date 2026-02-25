package com.itq.document_management_service.model;

import com.itq.document_management_service.reference.DocumentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "t_document")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private UUID documentNumber;

    @Column(nullable = false)
    private UUID createdBy;

    @Column(nullable = false)
    @Size(min = 1, max = 255)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DocumentStatus status = DocumentStatus.DRAFT;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "document")
    private List<DocumentHistory> history;

    @OneToOne(mappedBy = "document")
    private DocumentRegistry documentRegistry;

    @PrePersist
    private void generateDocumentNumber() {
        if (documentNumber == null) {
            documentNumber = UUID.randomUUID();
        }
    }
}
