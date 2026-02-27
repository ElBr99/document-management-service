package com.itq.document_management_service.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itq.document_management_service.config.TestContainerConfiguration;
import com.itq.document_management_service.dto.request.ChangeDocumentStatusDto;
import com.itq.document_management_service.dto.request.CreateDocumentMetadataDto;
import com.itq.document_management_service.mapper.DocumentHistoryMapper;
import com.itq.document_management_service.mapper.DocumentMapper;
import com.itq.document_management_service.mapper.DocumentRegistryMapper;
import com.itq.document_management_service.model.Document;
import com.itq.document_management_service.model.DocumentRegistry;
import com.itq.document_management_service.reference.DocumentStatus;
import com.itq.document_management_service.repository.DocumentHistoryRepository;
import com.itq.document_management_service.repository.DocumentRegistryRepository;
import com.itq.document_management_service.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;

import static com.itq.document_management_service.reference.DocumentStatus.SUBMITTED;
import static com.itq.document_management_service.reference.SubmissionResult.CONFLICT_STATUS;
import static com.itq.document_management_service.reference.SubmissionResult.SUCCESS;
import static com.itq.document_management_service.reference.UserAction.APPROVE;
import static com.itq.document_management_service.reference.UserAction.CREATE;
import static com.itq.document_management_service.reference.UserAction.SUBMIT;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@RequiredArgsConstructor
@AutoConfigureMockMvc
@Import(TestContainerConfiguration.class)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
public class DocumentProcessingServiceTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final DocumentRepository documentRepository;

    @MockitoSpyBean
    private final DocumentRegistryRepository registryRepository;
    private final DocumentHistoryRepository documentHistoryRepository;

    @MockitoSpyBean
    private final DocumentMapper documentMapper;

    @MockitoSpyBean
    private final DocumentHistoryMapper documentHistoryMapper;

    @MockitoSpyBean
    private final DocumentRegistryMapper documentRegistryMapper;

    @AfterEach
    void clearDb() {
        registryRepository.deleteAll();
        documentRepository.deleteAll();
        documentHistoryRepository.deleteAll();
    }

    @AfterEach
    void resetMocks() {
        reset(registryRepository, documentMapper, documentHistoryMapper, documentRegistryMapper);
    }


    @Test
    void documentLifecycleSuccess() throws Exception {
        UUID createdBy = UUID.fromString("f5124453-84d9-4410-af8e-645735fce629");
        CreateDocumentMetadataDto createDocMetadata = CreateDocumentMetadataDto.builder()
                .createdBy(createdBy)
                .title("title")
                .build();

        Long createdDocumentId = createDocumentAndCheckResults(createDocMetadata);


        List<Long> documentIdForStatusUpdating = List.of(createdDocumentId);
        UUID updatedBy = UUID.fromString("d11d335f-4154-437e-bd19-c6e09a927a44");
        ChangeDocumentStatusDto changeDocStatus = ChangeDocumentStatusDto.builder()
                .documentIds(documentIdForStatusUpdating)
                .build();

        submitDocAndCheckResults(changeDocStatus, updatedBy, createdDocumentId);


        approveDocumentAndCheckResults(changeDocStatus, updatedBy, createdDocumentId);

        var approvedDoc = registryRepository.findByDocId(createdDocumentId).stream().findFirst();
        assertFalse(approvedDoc.isEmpty());
        assertEquals(approvedDoc.get().getStatus(), DocumentStatus.APPROVED);

    }


    @Test
    void errorAddingDocumentToHistoryRegistry_thenApprovementRollback() throws Exception {
        UUID createdBy = UUID.fromString("f5124453-84d9-4410-af8e-645735fce629");
        CreateDocumentMetadataDto createDocMetadata = CreateDocumentMetadataDto.builder()
                .createdBy(createdBy)
                .title("title")
                .build();

        Long createdDocumentId = createDocumentAndCheckResults(createDocMetadata);


        List<Long> documentIdForStatusUpdating = List.of(createdDocumentId);
        UUID updatedBy = UUID.fromString("d11d335f-4154-437e-bd19-c6e09a927a44");
        ChangeDocumentStatusDto changeDocStatus = ChangeDocumentStatusDto.builder()
                .documentIds(documentIdForStatusUpdating)
                .build();

        submitDocAndCheckResults(changeDocStatus, updatedBy, createdDocumentId);

        doThrow(RuntimeException.class)
                .when(registryRepository)
                .save(ArgumentMatchers.any(DocumentRegistry.class));


        approveDocumentWithRollback(changeDocStatus, updatedBy, createdDocumentId);

        assertTrue(registryRepository.findAll().isEmpty());
    }

    @Test
    @Sql(value = "classpath:scripts/01_insert_docs_batchSubmit.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void batchSubmit() throws Exception {
        UUID updatedBy = UUID.fromString("f5124453-84d9-4410-af8e-645735fce629");

        var ids = documentRepository.findAll().stream()
                .map(document -> document.getId())
                .toList();

        ChangeDocumentStatusDto changeDocStatus = ChangeDocumentStatusDto.builder()
                .documentIds(ids)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/documents/submit/batch")
                        .param("updatedBy", String.valueOf(updatedBy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeDocStatus))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].resultMessage", Matchers.is(SUCCESS.toString())))
                .andExpect(jsonPath("$[1].resultMessage", Matchers.is(SUCCESS.toString())))
                .andExpect(jsonPath("$[2].resultMessage", Matchers.is(SUCCESS.toString())))
                .andReturn();

        documentRepository.findAllById(ids).stream()
                .forEach(document -> {
                    assertEquals(document.getStatus().name(), SUBMITTED.name());
                });
    }

    @Test
    @Sql(value = "classpath:scripts/02_insert_docs_partialApprove.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
    void batchPartialApprove() throws Exception {
        UUID updatedBy = UUID.fromString("f5124453-84d9-4410-af8e-645735fce629");

        var ids = documentRepository.findAll().stream()
                .map(document -> document.getId())
                .toList();

        ChangeDocumentStatusDto changeDocStatus = ChangeDocumentStatusDto.builder()
                .documentIds(ids)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/documents/approve/batch")
                        .param("updatedBy", String.valueOf(updatedBy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeDocStatus))
                )
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].resultMessage", Matchers.is(CONFLICT_STATUS.toString())))
                .andExpect(jsonPath("$[1].resultMessage", Matchers.is(SUCCESS.toString())))
                .andExpect(jsonPath("$[2].resultMessage", Matchers.is(SUCCESS.toString())))
                .andReturn();


        var approvedDocs = documentRepository.findAllById(ids).stream()
                .filter(document -> document.getStatus().equals(DocumentStatus.APPROVED))
                .toList();

        assertEquals(approvedDocs.size(), 2);
        verify(registryRepository, times(2)).save(any());

        var unapprovedDoc = documentRepository.findAllById(ids).stream()
                .filter(document -> document.getStatus().equals(DocumentStatus.DRAFT))
                .toList();
        assertEquals(unapprovedDoc.size(), 1);
    }

    private Long createDocumentAndCheckResults(CreateDocumentMetadataDto createDocMetadata) throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.post("/api/documents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDocMetadata))
                )
                .andExpect(status().is2xxSuccessful());

        var createdDoc = documentRepository.findAll().stream().findFirst();

        assertFalse(createdDoc.isEmpty());
        var existingCreatedDoc = createdDoc.get();
        assertEquals(existingCreatedDoc.getStatus(), DocumentStatus.DRAFT);
        var createdDocHistory = documentHistoryRepository.findByDocId(existingCreatedDoc.getId());
        assertFalse(createdDocHistory.isEmpty());
        createdDocHistory.forEach(documentHistory -> {
            assertEquals(documentHistory.getDocument().getId(), existingCreatedDoc.getId());
            assertEquals(documentHistory.getAction().name(), CREATE.name());
        });

        return existingCreatedDoc.getId();
    }

    private void submitDocAndCheckResults(ChangeDocumentStatusDto changeDocStatus, UUID updatedBy, Long id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/documents/submit/batch")
                        .param("updatedBy", String.valueOf(updatedBy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeDocStatus))
                )
                .andExpect(status().is2xxSuccessful())
                .andReturn();

        var submittedDoc = documentRepository.findByDocId(changeDocStatus.getDocumentIds().stream().findFirst().get());

        assertFalse(submittedDoc.isEmpty());
        var updatedExistingDocument = submittedDoc.get();
        assertEquals(SUBMITTED, updatedExistingDocument.getStatus());
        assertEquals(updatedExistingDocument.getId(), id);
        var submitHistory = updatedExistingDocument.getHistory().stream()
                .filter(documentHistory -> documentHistory.getAction().name().equalsIgnoreCase(SUBMIT.name()))
                .toList();
        assertFalse(submitHistory.isEmpty());
        assertEquals(1, submitHistory.size());
    }

    private Document approveDocumentAndCheckResults(ChangeDocumentStatusDto changeDocStatus, UUID updatedBy, Long id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/documents/approve/batch")
                        .param("updatedBy", String.valueOf(updatedBy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeDocStatus))
                )
                .andExpect(status().is2xxSuccessful());

        var finalDocObject = documentRepository.findByDocId(id);

        assertFalse(finalDocObject.isEmpty());
        var existingFinalDocument = finalDocObject.get();
        assertEquals(DocumentStatus.APPROVED.name(), existingFinalDocument.getStatus().name());
        assertEquals(existingFinalDocument.getId(), id);

        var approveHistory = existingFinalDocument.getHistory().stream()
                .filter(documentHistory -> documentHistory.getAction().name().equalsIgnoreCase(APPROVE.name()))
                .toList();
        assertFalse(approveHistory.isEmpty());
        assertEquals(1, approveHistory.size());

        return existingFinalDocument;
    }

    private Document approveDocumentWithRollback(ChangeDocumentStatusDto changeDocStatus, UUID updatedBy, Long id) throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/api/documents/approve/batch")
                        .param("updatedBy", String.valueOf(updatedBy))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(changeDocStatus))
                )
                .andExpect(status().is2xxSuccessful());

        var finalDocObject = documentRepository.findByDocId(id);

        assertFalse(finalDocObject.isEmpty());
        var existingFinalDocument = finalDocObject.get();
        assertEquals(SUBMITTED.name(), existingFinalDocument.getStatus().name());
        assertEquals(existingFinalDocument.getId(), id);

        var approveHistory = existingFinalDocument.getHistory().stream()
                .filter(documentHistory -> documentHistory.getAction().name().equalsIgnoreCase(APPROVE.name()))
                .toList();
        assertTrue(approveHistory.isEmpty());

        return existingFinalDocument;
    }
}
