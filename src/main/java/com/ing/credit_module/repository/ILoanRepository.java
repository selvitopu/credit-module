package com.ing.credit_module.repository;

import com.ing.credit_module.model.Loan;
import com.ing.credit_module.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ILoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findLoansByUser(User user);

    List<Loan> findLoansByUserAndNumberOfInstallmentsAndIsPaid(User user, int numberOfInstallments, boolean paid);
}
