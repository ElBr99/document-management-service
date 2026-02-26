package com.itq.document_management_service.dto.response;

import com.itq.document_management_service.reference.DocumentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentResponseDto {
    private Long id;
    private UUID documentNumber;
    private UUID createdBy;
    private String title;
    private DocumentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<HistoryResponse> history;

    @Data
    public static class HistoryResponse {
        private Long id;
        private String actor;
        private String action;
        private LocalDateTime performedAt;
        private String comment;
    }
}
