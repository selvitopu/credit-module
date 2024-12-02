package com.ing.credit_module.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.credit_module.config.UserContextHolder;
import com.ing.credit_module.dto.LoanDTO;
import com.ing.credit_module.exception.CreditBusinessRuleException;
import com.ing.credit_module.exception.UserNotFoundException;
import com.ing.credit_module.model.Loan;
import com.ing.credit_module.model.User;
import com.ing.credit_module.repository.ILoanRepository;
import com.ing.credit_module.service.ILoanService;
import com.ing.credit_module.service.IUserService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.ing.credit_module.constants.ServiceConstants.LOAN_SERVICE;

@Service(value = LOAN_SERVICE)
public class LoanService implements ILoanService {

    private ILoanRepository repository;
    private ObjectMapper objectMapper;
    private IUserService userService;

    @Lazy
    @Autowired
    public LoanService(ILoanRepository repository, ObjectMapper objectMapper, IUserService userService) {
        this.repository = repository;
        this.objectMapper = objectMapper;
        this.userService = userService;
    }

    @Override
    public List<Loan> findLoansByCustomer(User user) {
        return repository.findLoansByUser(user);
    }

    @Override
    public ResponseEntity<List<LoanDTO>> getLoansByUser(boolean isPaid, Integer numberOfInstallments) {
        User user = UserContextHolder.getUser();

        if (Objects.isNull(user)) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Loan> loans = repository.findLoansByUserAndNumberOfInstallmentsAndIsPaid(user, numberOfInstallments, isPaid);

        if (loans.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        List<LoanDTO> loanDTOS = loans.stream().map(loan -> objectMapper.convertValue(loan, LoanDTO.class)).collect(Collectors.toList());
        return new ResponseEntity<>(loanDTOS, HttpStatus.OK);
    }

    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class, CreditBusinessRuleException.class} )
    public ResponseEntity<LoanDTO> createLoan(LoanDTO loanDTO) {
        Loan loan = objectMapper.convertValue(loanDTO, Loan.class);

        User user = UserContextHolder.getUser();
        BigDecimal userLimit = user.getCreditLimit();

        if (userLimit.compareTo(loan.getAmount()) < 0) {
            throw new CreditBusinessRuleException("User credit limit exceeded");
        }
        loan.setUser(user);
        loan.setLoanInstallment(loan.createLoanInstallments());

        user.setUsedCreditLimit(loan.getAmount());

        userService.saveUser(user);
        loan = repository.save(loan);

        return new ResponseEntity<>(objectMapper.convertValue(loan, LoanDTO.class), HttpStatus.CREATED);
    }

    @Override
    public ResponseEntity<String> deleteLoan(Long loanId) {
        Optional<Loan> optionalLoan = repository.findById(loanId);

        if (optionalLoan.isEmpty())
            return new ResponseEntity<>("Not found!", HttpStatus.NOT_FOUND);

        Loan loan = optionalLoan.get();

        repository.delete(loan);

        return new ResponseEntity<>("Deleted!", HttpStatus.OK);
    }

}
