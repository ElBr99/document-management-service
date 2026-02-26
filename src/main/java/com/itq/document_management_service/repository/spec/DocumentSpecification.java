package com.itq.document_management_service.repository.spec;

import static com.itq.document_management_service.model.Document_.createdAt;
import static com.itq.document_management_service.model.Document_.createdBy;
import static com.itq.document_management_service.model.Document_.status;

import com.itq.document_management_service.dto.request.DocumentSearchRequest;
import com.itq.document_management_service.model.Document;
import jakarta.persistence.criteria.Predicate;
import lombok.experimental.UtilityClass;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class DocumentSpecification {

    public static Specification<Document> from(DocumentSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get(status), request.getStatus()));
            }

            if (request.getCreatedBy() != null) {
                predicates.add(cb.equal(root.get(createdBy), request.getCreatedBy()));
            }

            if (request.getDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(createdAt), request.getDateFrom()));
            }

            if (request.getDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(createdAt), request.getDateTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
