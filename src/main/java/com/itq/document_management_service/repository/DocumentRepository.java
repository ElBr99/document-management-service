package com.itq.document_management_service.repository;

import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.reference.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    @Modifying (flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Document d SET d.status = :newStatus " +
            "WHERE d.id = :id AND d.status = :expectedStatus")
    int updateStatusById(@Param("id") Long id,
                         @Param("expectedStatus") DocumentStatus expectedStatus,
                         @Param("newStatus") DocumentStatus newStatus);





}
