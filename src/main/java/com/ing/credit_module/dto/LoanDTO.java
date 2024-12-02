package com.ing.credit_module.dto;

import com.ing.credit_module.model.LoanInstallment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoanDTO implements Serializable {
    private Long id;
    private UserDTO user;
    private BigDecimal amount;
    private Double interestRate;
    private Integer numberOfInstallments;
    private List<LoanInstallmentDTO> loanInstallment;
    private boolean isPaid;

}
