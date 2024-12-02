package com.ing.credit_module.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.ing.credit_module.exception.CreditBusinessRuleException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.ing.credit_module.constants.TableConstants.LOAN;


@Getter
@Entity
@Table(name = LOAN)
@NoArgsConstructor
public class Loan extends AbstractEntity {

    private static final List<Integer> permittedNumberOfInstallments = List.of(6, 9, 12, 24);
    private static final Double INTEREST_RATE_MIN = 0.1;
    private static final Double INTEREST_RATE_MAX = 0.5;

    @Setter
    @JsonBackReference
    @ManyToOne(cascade = CascadeType.MERGE)
    private User user;

    @Setter
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    private Double interestRate;

    @Column(name = "number_of_installments", nullable = false)
    private Integer numberOfInstallments;

    @Setter
    @JsonManagedReference
    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
    private List<LoanInstallment> loanInstallment;

    @Setter
    private boolean isPaid;

    public void setNumberOfInstallments(Integer numberOfInstallments) {
        if (!permittedNumberOfInstallments.contains(numberOfInstallments)) {
            throw new CreditBusinessRuleException("Number of Installments must be chose one of 6, 9, 12 or 24");
        }
        this.numberOfInstallments = numberOfInstallments;
    }

    public void setInterestRate(double interestRate) {
        if (interestRate < INTEREST_RATE_MIN || interestRate > INTEREST_RATE_MAX) {
            throw new CreditBusinessRuleException("Interest rate must be between 0.1 and 0.5");
        }
        this.interestRate = interestRate;
    }

    @Transient
    public BigDecimal getPaybackAmount() {
        return this.amount.multiply(new BigDecimal(this.interestRate).add(BigDecimal.ONE)).setScale(10, RoundingMode.HALF_UP);
    }

    @Transient
    public List<LoanInstallment> createLoanInstallments() {

        List<LoanInstallment> loanInstallments = new ArrayList<>();

        BigDecimal paybackAmount = getPaybackAmount();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < this.numberOfInstallments; i++) {
            LoanInstallment loanInstallment = new LoanInstallment();
            loanInstallment.setLoan(this);
            loanInstallment.setAmount(paybackAmount.divide(new BigDecimal(this.numberOfInstallments), 10, RoundingMode.HALF_UP));
            loanInstallment.setPaid(false);
            loanInstallment.setDueDate(now.plusMonths(i).withDayOfMonth(1));
            loanInstallments.add(loanInstallment);
        }
        return loanInstallments;
    }
}
