package com.itq.document_management_service.dto.request;

import com.itq.document_management_service.utils.ApiAnswerConstants;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ConcurrencyTestApiDto {

    @NotNull(message = ApiAnswerConstants.MISSING_VALUE + " значение парметра docId не должно быть пустым")
    @Min(value = 1)
    @Max(value = 1)
    //  ограничение на мах =1 для целей тестирвоания
    private List<Long> docIds;

    @NotNull(message = ApiAnswerConstants.MISSING_VALUE + " значение парметра attempts не должно быть пустым")
    @Min(value = 2, message = ApiAnswerConstants.INCORRECT_VALUE + " минимальное значение параметра attempts должно быть не менее 2-х")
    @Max(value = 5, message = ApiAnswerConstants.INCORRECT_VALUE + " максимальное значение параметра attempts должно быть не более 5-ти")
    private int attempts;

    @NotNull(message = ApiAnswerConstants.MISSING_VALUE + " значение парметра threadAmount не должно быть пустым")
    @Min(value = 2, message = ApiAnswerConstants.INCORRECT_VALUE + " минимальное значение параметра threadAmount должно быть не менее 2-х")
    @Max(value = 5, message = ApiAnswerConstants.INCORRECT_VALUE + " максимальное значение параметра threadAmount должно быть не более 5-ти")
    private int threadAmount;

}
