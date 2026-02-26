package com.itq.document_management_service.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Data
//@Value
@Validated
@Component
@ConfigurationProperties(prefix = "document.job")
public class DocumentJobProperties {

    @NotNull
    @Max(1000)
    Integer moveToSubmittedBatchSize;

    @NotNull
    @Max(1000)
    Integer moveToApprovedBatchSize;

    @NotNull
    UUID updatedBy;
}