package com.itq.document_management_service.repository;

import com.itq.document_management_service.model.DocumentHistory;
import com.itq.document_management_service.model.DocumentRegistry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRegistryRepository extends JpaRepository<DocumentRegistry, Long> {


}
