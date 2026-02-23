package com.itq.document_management_service.dto.request;

import com.itq.document_management_service.utils.ApiAnswerConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitDocumentDto {

    @Size(min = 1, max = 1000, message = ApiAnswerConstants.INCORRECT_VALUE + "максимальное кол-во айдишников -1000, минимальное - 1")
    private List<@NotNull(message = ApiAnswerConstants.MISSING_VALUE + "значение списка не может быть пустым") Long> documentIds;

}
