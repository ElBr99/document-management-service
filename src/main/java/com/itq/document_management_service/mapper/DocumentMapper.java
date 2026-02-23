package com.itq.document_management_service.mapper;

import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import com.itq.document_management_service.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {

    Document mapToDocument(CreateDocumentMetadataDto createDocumentMetadataDto);

}
