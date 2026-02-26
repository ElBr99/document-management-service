package com.itq.document_management_service.mapper;

import com.itq.document_management_service.dto.request.DocumentRegistryDto;
import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.model.DocumentRegistry;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentRegistryMapper {
    DocumentRegistry mapToDocumentRegistry(DocumentRegistryDto documentRegistryDto);

    DocumentRegistryDto mapFromDocument (Document document);
}
