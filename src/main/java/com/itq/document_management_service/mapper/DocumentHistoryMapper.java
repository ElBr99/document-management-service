package com.itq.document_management_service.mapper;

import com.itq.document_management_service.dto.request.DocumentStatusHistoryDto;
import com.itq.document_management_service.model.DocumentHistory;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentHistoryMapper {

    DocumentHistory mapToDocument(DocumentStatusHistoryDto changeDocumentStatusDto);
}
