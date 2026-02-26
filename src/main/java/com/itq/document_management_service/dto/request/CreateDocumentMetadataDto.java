package com.itq.document_management_service.dto.request;

import com.itq.document_management_service.utils.ApiAnswerConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateDocumentMetadataDto {

    @NotNull(message = ApiAnswerConstants.MISSING_VALUE)
    private UUID createdBy;

    @NotBlank(message = ApiAnswerConstants.MISSING_VALUE)
    private String title;

}
