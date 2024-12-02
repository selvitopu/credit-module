package com.ing.credit_module.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.credit_module.config.UserContextHolder;
import com.ing.credit_module.dto.LoanDTO;
import com.ing.credit_module.dto.LoanInstallmentDTO;
import com.ing.credit_module.dto.LoanPaymentDTO;
import com.ing.credit_module.dto.LoanPaymentResultDTO;
import com.ing.credit_module.exception.CreditBusinessRuleException;
import com.ing.credit_module.exception.UserNotFoundException;
import com.ing.credit_module.model.Loan;
import com.ing.credit_module.model.LoanInstallment;
import com.ing.credit_module.model.User;
import com.ing.credit_module.repository.ILoanInstallmentRepository;
import com.ing.credit_module.service.ILoanInstallmentService;
import com.ing.credit_module.service.ILoanService;
import com.ing.credit_module.service.IUserService;
import com.ing.credit_module.specification.LoanInstallmentSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.ing.credit_module.constants.ServiceConstants.LOAN_INSTALLMENT_SERVICE;


@Service(value = LOAN_INSTALLMENT_SERVICE)
public class LoanInstallmentService implements ILoanInstallmentService {

    private ILoanInstallmentRepository repository;
    private IUserService userService;
    private ILoanService loanService;
    private ObjectMapper objectMapper;

    @Lazy
    @Autowired
    public LoanInstallmentService(ILoanInstallmentRepository repository, IUserService userService, ILoanService loanService, ObjectMapper objectMapper) {
        this.repository = repository;
        this.userService = userService;
        this.loanService = loanService;
        this.objectMapper = objectMapper;
    }

    private static final String ADMIN = "ADMIN";

    @Override
    public ResponseEntity<List<LoanInstallmentDTO>> getLoanInstallments(Long loanId) {
        User user = UserContextHolder.getUser();
        List<Loan> userLoans = loanService.findLoansByCustomer(user);

        if (user.getRoles().stream().noneMatch(r -> r.getAuthority().equals(ADMIN)) && !CollectionUtils.isEmpty(userLoans) && userLoans.stream().noneMatch(l -> l.getId().equals(loanId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<LoanInstallment> loanInstallments = repository.findByLoanId(loanId);

        if (loanInstallments.isEmpty())
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);

        List<LoanInstallmentDTO> loanInstallmentDTOS = loanInstallments
                .stream()
                .map(l -> objectMapper.convertValue(l, LoanInstallmentDTO.class))
                .collect(Collectors.toList());

        return new ResponseEntity<>(loanInstallmentDTOS, HttpStatus.OK);
    }

    @Override
    @Transactional(rollbackFor = {UserNotFoundException.class, CreditBusinessRuleException.class})
    public ResponseEntity<LoanPaymentResultDTO> payLoanInstallment(LoanPaymentDTO loanPaymentDTO) {
        User user = UserContextHolder.getUser();

        List<Loan> userLoans = loanService.findLoansByCustomer(user);

        if (user.getRoles().stream().noneMatch(r -> r.getAuthority().equals(ADMIN)) && !CollectionUtils.isEmpty(userLoans) && userLoans.stream().noneMatch(l -> l.getId().equals(loanPaymentDTO.getLoanId()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        List<LoanInstallment> loanInstallments = repository.findByLoanIdAndIsPaidIsOrderByCreatedTimeAsc(loanPaymentDTO.getLoanId(), false);

        if (loanInstallments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }

        BigDecimal wholePaidAmount = BigDecimal.ZERO;
        LoanPaymentResultDTO loanPaymentResultDTO = new LoanPaymentResultDTO();

        LoanInstallment loanInstallment = loanInstallments.get(0);

        List<LoanInstallment> toSaved = loanInstallment.payInstallment(loanPaymentDTO.getAmountToPay(), LocalDateTime.now(), new ArrayList<>());

        wholePaidAmount = wholePaidAmount.add(toSaved.stream().map(LoanInstallment::getPaidAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
        user.setUsedCreditLimit(user.getUsedCreditLimit().add(wholePaidAmount));

        try {
            userService.saveUser(user);
            repository.saveAll(toSaved);
            loanPaymentResultDTO.setLoanId(loanPaymentDTO.getLoanId());
            loanPaymentResultDTO.setTotalAmountPaid(wholePaidAmount);
            loanPaymentResultDTO.setPaidLoanInstallmentCount(toSaved.size());
            return new ResponseEntity<>(loanPaymentResultDTO, HttpStatus.OK);
        } catch (Exception e) {
            throw new CreditBusinessRuleException(e.getMessage());
        }
    }

    @Override
    public Page<LoanInstallmentDTO> getLoanInstallmentsPageable(Boolean isPaid, LocalDateTime dueDate, Long loanId, Pageable pageable) {
        Specification<LoanInstallment> spec = Specification.where(LoanInstallmentSpecification.isPaid(isPaid))
                .and(LoanInstallmentSpecification.dueDateBefore(dueDate))
                .and(LoanInstallmentSpecification.loanId(loanId));

        return repository.findAll(spec, pageable).map(payment ->
                new LoanInstallmentDTO(
                        payment.getId(),
                        objectMapper.convertValue(payment.getLoan(), LoanDTO.class),
                        payment.getAmount(),
                        payment.getPaidAmount(),
                        payment.getDueDate(),
                        payment.getPaymentDate(),
                        payment.isPaid()
                )
        );
    }

}
