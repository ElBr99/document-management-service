--Создание индексов на таблицу t_documents
CREATE INDEX IF NOT EXISTS idx_documents_status ON t_document (status);
CREATE INDEX IF NOT EXISTS idx_documents_created_by ON t_document (created_by);
CREATE INDEX IF NOT EXISTS idx_documents_created_at ON t_document (created_at);

--Создание индексов на таблицу t_document_history
CREATE INDEX IF NOT EXISTS idx_history_document_id ON t_document_history (doc_id);


--Создание индексов на таблицу t_document_registry
CREATE INDEX IF NOT EXISTS idx_registry_document_id ON t_document_registry (doc_id);

-- Для поискового запроса
CREATE INDEX IF NOT EXISTS idx_documents_search_spec
    ON t_document (status, created_by, created_at);