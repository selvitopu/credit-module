package com.ing.credit_module.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.ing.credit_module.exception.CreditBusinessRuleException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static com.ing.credit_module.constants.TableConstants.LOAN_INSTALLMENT;

@Getter
@Setter
@Entity
@Table(name = LOAN_INSTALLMENT)
@NoArgsConstructor
public class LoanInstallment extends AbstractEntity {

    private static final BigDecimal interestRate = new BigDecimal("0.001");
    private static final BigDecimal TWO = new BigDecimal("2");

    @JsonBackReference
    @ManyToOne(cascade = CascadeType.MERGE)
    private Loan loan;

    private BigDecimal amount;

    private BigDecimal paidAmount = BigDecimal.ZERO;

    private LocalDateTime dueDate;

    private LocalDateTime paymentDate;

    private boolean isPaid = false;

    @Transient
    public List<LoanInstallment> payInstallment(BigDecimal paidAmount, LocalDateTime paymentDate, List<LoanInstallment> installed) {
        BigDecimal interest = BigDecimal.ZERO;

        if (installed.isEmpty()) {
            interest = calculateInterest(paymentDate);
        }

        BigDecimal calculatedAmount = interest.add(this.amount);
        if (calculatedAmount.compareTo(paidAmount) > 0) {
            if (installed.isEmpty()) {
                throw new CreditBusinessRuleException("Paid amount should be greater than calculated amount");
            } else {
                return installed;
            }
        } else if (calculatedAmount.compareTo(paidAmount) < 0) {
            setInstallment(calculatedAmount, paymentDate);
            BigDecimal remainAmount = paidAmount.subtract(calculatedAmount);
            installed.add(this);
            if (remainAmount.compareTo(this.amount.multiply(TWO)) > 0) {
                payInstallment(this.amount.multiply(TWO), paymentDate, installed);
            } else {
                payInstallment(remainAmount, paymentDate, installed);
            }
            return installed;
        } else if (calculatedAmount.compareTo(paidAmount) == 0) {
            setInstallment(calculatedAmount, paymentDate);
            installed.add(this);
            return installed;
        }
        throw new CreditBusinessRuleException("Payment failed!");
    }

    @Transient
    public void setInstallment(BigDecimal paidAmount, LocalDateTime paymentDate) {
        this.paidAmount = paidAmount;
        this.isPaid = true;
        this.paymentDate = paymentDate;
    }

    @Transient
    public BigDecimal calculateInterest(LocalDateTime paymentDate) {
        if (this.dueDate.getDayOfMonth() == paymentDate.getDayOfMonth())
            return BigDecimal.ZERO;
        if (this.dueDate.isBefore(paymentDate) || this.dueDate.isAfter(paymentDate)) {
            Duration duration = Duration.between(this.dueDate, paymentDate);
            return interestRate.multiply(this.amount).multiply(new BigDecimal(duration.toDays()));
        }
        return BigDecimal.ZERO;
    }

}
