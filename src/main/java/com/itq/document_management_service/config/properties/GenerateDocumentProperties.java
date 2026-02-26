package com.itq.document_management_service.config.properties;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import java.util.UUID;


@Data
@Validated
@Component
@ConfigurationProperties(prefix = "util.generate-document")
public class GenerateDocumentProperties {
    @NotNull
    Integer batchSize;
    @NotBlank
    String title;
    @NotNull
    UUID createdBy;
}
