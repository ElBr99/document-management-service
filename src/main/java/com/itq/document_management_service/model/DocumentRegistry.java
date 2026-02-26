package com.itq.document_management_service.model;

import com.itq.document_management_service.reference.DocumentStatus;
import com.itq.document_management_service.reference.UserAction;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@ToString(exclude = {"document"})
@Table(name = "t_document_registry")
public class DocumentRegistry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne (cascade = CascadeType.REMOVE)
    @JoinColumn(name = "doc_id", referencedColumnName = "id")
    private Document document;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DocumentStatus status;

    @Column(nullable = false)
    private UUID registeredBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime registeredAt;

    @Column(name = "user_comment")
    private String comment;

}
