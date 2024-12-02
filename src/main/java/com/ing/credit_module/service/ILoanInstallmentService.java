package com.ing.credit_module.service;

import com.ing.credit_module.dto.LoanInstallmentDTO;
import com.ing.credit_module.dto.LoanPaymentDTO;
import com.ing.credit_module.dto.LoanPaymentResultDTO;
import com.ing.credit_module.model.Loan;
import com.ing.credit_module.model.LoanInstallment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface ILoanInstallmentService {

    ResponseEntity<List<LoanInstallmentDTO>> getLoanInstallments(Long loanId);

    ResponseEntity<LoanPaymentResultDTO> payLoanInstallment(LoanPaymentDTO loanPaymentDTO);

    Page<LoanInstallmentDTO> getLoanInstallmentsPageable(Boolean isPaid, LocalDateTime dueDate, Long loanId, Pageable pageable);
}
