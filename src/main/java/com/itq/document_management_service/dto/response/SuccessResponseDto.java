package com.itq.document_management_service.dto.response;

import com.itq.document_management_service.dto.AbstractResponseDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

@SuperBuilder
@Getter
@NoArgsConstructor
public class SuccessResponseDto extends AbstractResponseDto {

    public SuccessResponseDto (String message) {
        super(HttpStatus.OK, message);

    }
}
