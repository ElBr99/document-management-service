package com.itq.document_management_service.repository;

import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.DocumentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long>, JpaSpecificationExecutor<Document>, PagingAndSortingRepository<Document, Long> {

    @Transactional
    @Query(value = """
              UPDATE t_document
              SET status = :newStatus
              WHERE id = (SELECT td.id FROM t_document td
                          WHERE td.id = :id AND td.status = :expectedStatus FOR UPDATE
              )
              RETURNING *;
            """, nativeQuery = true)
    Document updateStatusById(@Param("id") Long id,
                                        @Param("expectedStatus") String expectedStatus,
                                        @Param("newStatus") String newStatus);


    @Query("SELECT d FROM Document d JOIN FETCH d.history where d.id = :id")
    Optional<Document> findByDocId(@Param("id") Long id);


    Page<Document> findAllByIdIn(List<Long> ids, Pageable pageable);

    @Query("""
            SELECT id
            FROM Document d
            WHERE d.status = :status
            """)
    List<Long> findIdsByStatus(DocumentStatus status, Pageable pageable);


}
