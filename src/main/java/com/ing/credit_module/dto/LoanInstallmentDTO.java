package com.ing.credit_module.dto;

import com.ing.credit_module.model.Loan;
import com.ing.credit_module.model.LoanInstallment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanInstallmentDTO implements Serializable {
    private Long id;
    private LoanDTO loan;
    private BigDecimal amount;
    private BigDecimal paidAmount;
    private LocalDateTime dueDate;
    private LocalDateTime paymentDate;
    private boolean isPaid;
}
