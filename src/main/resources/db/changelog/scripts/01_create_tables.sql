-- Создание таблицы t_document

CREATE TABLE IF NOT EXISTS t_document (
    id BIGSERIAL PRIMARY KEY,
    document_number UUID NOT NULL UNIQUE,
    created_by UUID NOT NULL,
    title VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'DRAFT',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT check_title_length CHECK ((LENGTH(title) >= 1 AND LENGTH(title) <= 255)),
    CONSTRAINT check_status_value CHECK (status IN ('DRAFT', 'SUBMITTED', 'APPROVED'))
);

-- Создание таблицы t_document_history
 CREATE TABLE IF NOT EXISTS t_document_history (
     id BIGSERIAL PRIMARY KEY,
     doc_id BIGINT NOT NULL REFERENCES t_document(id) ON DELETE CASCADE,
     updated_by UUID NOT NULL,
     updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
     user_action VARCHAR(32) NOT NULL,
     user_comment TEXT,
     CONSTRAINT check_user_action CHECK (user_action IN ('CREATE', 'SUBMIT', 'APPROVE'))
 );

 -- Создание таблицы t_document_registry
 CREATE TABLE IF NOT EXISTS t_document_registry (
     id BIGSERIAL PRIMARY KEY,
     doc_id BIGINT NOT NULL UNIQUE REFERENCES t_document(id),
     status VARCHAR(32) NOT NULL,
     registered_by UUID NOT NULL,
     registered_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
     user_comment TEXT,
     CONSTRAINT check_status CHECK (status IN ('APPROVED'))
 );