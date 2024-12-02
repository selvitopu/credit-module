package com.ing.credit_module.specification;

import com.ing.credit_module.model.LoanInstallment;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class LoanInstallmentSpecification {

    public static Specification<LoanInstallment> isPaid(Boolean isPaid) {
        return (root, query, criteriaBuilder) -> isPaid == null ? null : criteriaBuilder.equal(root.get("isPaid"), isPaid);
    }

    public static Specification<LoanInstallment> dueDateBefore(LocalDateTime dueDate) {
        return (root, query, criteriaBuilder) -> dueDate == null ? null : criteriaBuilder.lessThan(root.get("dueDate"), dueDate);
    }

    public static Specification<LoanInstallment> loanId(Long loanId) {
        return (root, query, criteriaBuilder) -> loanId == null ? null : criteriaBuilder.equal(root.get("loan").get("id"), loanId);
    }
}