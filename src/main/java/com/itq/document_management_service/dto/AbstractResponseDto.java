package com.itq.document_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Data
public abstract class AbstractResponseDto {
    private HttpStatus statusCode;
    private String message;
}
