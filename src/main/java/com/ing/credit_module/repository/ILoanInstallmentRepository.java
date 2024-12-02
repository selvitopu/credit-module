package com.ing.credit_module.repository;

import com.ing.credit_module.model.LoanInstallment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILoanInstallmentRepository extends JpaRepository<LoanInstallment, Long>, JpaSpecificationExecutor<LoanInstallment> {
    List<LoanInstallment> findByLoanId(Long loanId);

    List<LoanInstallment> findByLoanIdAndIsPaidIsOrderByCreatedTimeAsc(Long loanId, boolean paid);
}
