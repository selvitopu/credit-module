package com.ing.credit_module.service;

import com.ing.credit_module.dto.LoanDTO;
import com.ing.credit_module.model.Loan;
import com.ing.credit_module.model.User;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface ILoanService {

    List<Loan> findLoansByCustomer(User user);

    ResponseEntity<List<LoanDTO>> getLoansByUser(boolean isPaid, Integer numberOfInstallments);

    ResponseEntity<LoanDTO> createLoan(LoanDTO loanDTO);

    ResponseEntity<String> deleteLoan(Long loanId);
}
