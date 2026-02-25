package com.itq.document_management_service.dto.response;

import com.itq.document_management_service.reference.SubmissionResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionResultsDto {

    private Long documentId;
    private SubmissionResult resultMessage;

}
