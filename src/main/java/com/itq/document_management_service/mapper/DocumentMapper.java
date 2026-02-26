package com.itq.document_management_service.mapper;

import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import com.itq.document_management_service.dto.response.DocumentResponseDto;
import com.itq.document_management_service.model.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {

    Document mapToDocument(CreateDocumentMetadataDto createDocumentMetadataDto);

    DocumentResponseDto mapWithHistoryFromDocument(Document document);

    @Mapping(target="history", ignore = true)
    DocumentResponseDto mapWithoutHistoryFromDoc(Document document);

}
