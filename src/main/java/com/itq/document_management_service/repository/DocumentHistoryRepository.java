package com.itq.document_management_service.repository;

import com.itq.document_management_service.model.DocumentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentHistoryRepository extends JpaRepository<DocumentHistory, Long> {

    @Query(value = "SELECT * FROM t_document_history WHERE doc_id = :docId", nativeQuery = true)
    List<DocumentHistory> findByDocId(@Param("docId")Long docId);

}
