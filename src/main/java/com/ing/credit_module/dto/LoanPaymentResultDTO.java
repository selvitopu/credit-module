package com.ing.credit_module.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanPaymentResultDTO implements Serializable {

    private Long loanId;
    private BigDecimal totalAmountPaid;
    private Integer paidLoanInstallmentCount;

}
