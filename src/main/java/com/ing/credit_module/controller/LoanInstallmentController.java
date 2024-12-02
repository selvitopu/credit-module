package com.ing.credit_module.controller;


import com.ing.credit_module.dto.LoanInstallmentDTO;
import com.ing.credit_module.dto.LoanPaymentDTO;
import com.ing.credit_module.dto.LoanPaymentResultDTO;
import com.ing.credit_module.service.ILoanInstallmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static com.ing.credit_module.constants.ControllerConstants.LOAN_INSTALLMENT_CONTROLLER;

@RestController
@RequiredArgsConstructor
@RequestMapping(LOAN_INSTALLMENT_CONTROLLER)
public class LoanInstallmentController {

    private final ILoanInstallmentService loanInstallmentService;

    @GetMapping("")
    public ResponseEntity<List<LoanInstallmentDTO>> getLoansByUser(@RequestParam Long loanId) {
        return loanInstallmentService.getLoanInstallments(loanId);
    }

    @PutMapping("")
    public ResponseEntity<LoanPaymentResultDTO> payLoanInstallment(@RequestBody LoanPaymentDTO loanPaymentDTO) {
        return loanInstallmentService.payLoanInstallment(loanPaymentDTO);
    }

    @GetMapping("/pageable")
    public Page<LoanInstallmentDTO> getLoanInstallmentsPageable(
            @RequestParam(required = false) Boolean isPaid,
            @RequestParam(required = false) LocalDateTime dueDate,
            @RequestParam(required = false) Long loanId,
            Pageable pageable
    ) {
        return loanInstallmentService.getLoanInstallmentsPageable(isPaid, dueDate, loanId, pageable);
    }
}
