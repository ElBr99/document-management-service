package com.itq.document_management_service.repository;

import com.itq.document_management_service.model.DocumentRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRegistryRepository extends JpaRepository<DocumentRegistry, Long> {


    @Query(value = "SELECT * FROM t_document_registry WHERE doc_id = :docId", nativeQuery = true)
    List<DocumentRegistry> findByDocId(@Param("docId")Long docId);

}
