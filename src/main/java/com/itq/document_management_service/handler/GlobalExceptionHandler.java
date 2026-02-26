package com.itq.document_management_service.handler;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.itq.document_management_service.dto.AbstractResponseDto;
import com.itq.document_management_service.dto.response.ErrorResponseDto;
import com.itq.document_management_service.exception.ChangeDocumentStatusConflictException;
import com.itq.document_management_service.exception.DocumentNotFoundException;
import com.itq.document_management_service.exception.DocumentRegistrySavingException;
import com.itq.document_management_service.utils.ApiAnswerConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentNotFoundException.class)
    public ResponseEntity<AbstractResponseDto> handleNotFoundExceptions(RuntimeException ex) {
        log.info("Обработчик ошибок поймал DocumentNotFoundException: {}", ex.getMessage());
        return ResponseEntity.status(NOT_FOUND)
                .body(new ErrorResponseDto(NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler({ChangeDocumentStatusConflictException.class,
            DocumentRegistrySavingException.class
    })
    public ResponseEntity<AbstractResponseDto> handleStatusTransferringExceptions(RuntimeException ex) {
        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponseDto(BAD_REQUEST, ex.getMessage()));
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<AbstractResponseDto> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getAllErrors().stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponseDto(BAD_REQUEST, message));
    }

    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<AbstractResponseDto> handleInvalidFormatException(InvalidFormatException ex) {
        String errorMessage = ex.getPath().stream().map(JsonMappingException.Reference::getFieldName)
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(BAD_REQUEST)
                .body(new ErrorResponseDto(BAD_REQUEST, errorMessage));
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<AbstractResponseDto> handleGeneralException(Exception ex) {
        log.warn("Внутрення ошибка сервера: ", ex);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(new ErrorResponseDto(INTERNAL_SERVER_ERROR,ApiAnswerConstants.INTERNAL_SERVER_ERROR + ex.getMessage()));
    }
}
