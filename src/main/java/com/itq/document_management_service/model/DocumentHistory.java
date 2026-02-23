package com.itq.document_management_service.model;

import com.itq.document_management_service.reference.UserAction;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "t_document_history")
public class DocumentHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "doc_id", nullable = false)
    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "doc_id")
    private UUID document;

    @Column(nullable = false)
    private UUID updatedBy;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "user_action", nullable = false)
    @Enumerated(EnumType.STRING)
    private UserAction action;

    @Column(name = "user_comment")
    private String comment;
}
