package com.ing.credit_module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.credit_module.config.UserContextHolder;
import com.ing.credit_module.dto.LoanInstallmentDTO;
import com.ing.credit_module.dto.LoanPaymentDTO;
import com.ing.credit_module.dto.LoanPaymentResultDTO;
import com.ing.credit_module.model.Loan;
import com.ing.credit_module.model.LoanInstallment;
import com.ing.credit_module.model.User;
import com.ing.credit_module.repository.ILoanInstallmentRepository;
import com.ing.credit_module.service.ILoanService;
import com.ing.credit_module.service.IUserService;
import com.ing.credit_module.service.impl.LoanInstallmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class LoanInstallmentServiceTest {

    @Mock
    private ILoanInstallmentRepository repository;

    @Mock
    private IUserService userService;

    @Mock
    private ILoanService loanService;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private LoanInstallmentService loanInstallmentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetLoanInstallments_Forbidden() {
        User user = new User();
        user.setRoles(new HashSet<>());
        Loan loan = new Loan();
        loan.setId(1L);

        try (MockedStatic<UserContextHolder> mockedStatic = mockStatic(UserContextHolder.class)) {
            mockedStatic.when(UserContextHolder::getUser).thenReturn(user);
            when(loanService.findLoansByCustomer(user)).thenReturn(List.of(loan));
            ResponseEntity<List<LoanInstallmentDTO>> response = loanInstallmentService.getLoanInstallments(2L);

            assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        }
    }

    @Test
    void testGetLoanInstallments_NoContent() {
        User user = new User();
        Loan loan = new Loan();
        loan.setId(1L);

        try (MockedStatic<UserContextHolder> mockedStatic = mockStatic(UserContextHolder.class)) {
            mockedStatic.when(UserContextHolder::getUser).thenReturn(user);
            when(loanService.findLoansByCustomer(user)).thenReturn(List.of(loan));
            when(repository.findByLoanId(1L)).thenReturn(Collections.emptyList());

            ResponseEntity<List<LoanInstallmentDTO>> response = loanInstallmentService.getLoanInstallments(1L);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Test
    void testGetLoanInstallments_Success() {
        User user = new User();
        Loan loan = new Loan();
        loan.setId(1L);
        LoanInstallment installment = new LoanInstallment();

        try (MockedStatic<UserContextHolder> mockedStatic = mockStatic(UserContextHolder.class)) {
            mockedStatic.when(UserContextHolder::getUser).thenReturn(user);
            when(loanService.findLoansByCustomer(user)).thenReturn(List.of(loan));
            when(repository.findByLoanId(1L)).thenReturn(List.of(installment));
            LoanInstallmentDTO installmentDTO = new LoanInstallmentDTO();
            when(objectMapper.convertValue(installment, LoanInstallmentDTO.class)).thenReturn(installmentDTO);

            ResponseEntity<List<LoanInstallmentDTO>> response = loanInstallmentService.getLoanInstallments(1L);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertNotNull(response.getBody());
            assertEquals(1, response.getBody().size());
        }
    }

    @Test
    void testPayLoanInstallment_NoContent() {
        LoanPaymentDTO paymentDTO = new LoanPaymentDTO();
        paymentDTO.setLoanId(1L);
        User user = new User();

        try (MockedStatic<UserContextHolder> mockedStatic = mockStatic(UserContextHolder.class)) {
            mockedStatic.when(UserContextHolder::getUser).thenReturn(user);
            when(loanService.findLoansByCustomer(user)).thenReturn(Collections.emptyList());
            when(repository.findByLoanIdAndIsPaidIsOrderByCreatedTimeAsc(1L, false)).thenReturn(Collections.emptyList());

            ResponseEntity<LoanPaymentResultDTO> response = loanInstallmentService.payLoanInstallment(paymentDTO);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Test
    void testPayLoanInstallment_Success() {
        LoanPaymentDTO paymentDTO = new LoanPaymentDTO();
        paymentDTO.setLoanId(1L);
        paymentDTO.setAmountToPay(BigDecimal.valueOf(1000));

        User user = new User();
        user.setUsedCreditLimit(BigDecimal.ZERO);

        LoanInstallment installment = mock(LoanInstallment.class);
        when(installment.getPaidAmount()).thenReturn(BigDecimal.ZERO);
        when(installment.getAmount()).thenReturn(BigDecimal.valueOf(1000));
        when(installment.payInstallment(any(), any(), any())).thenReturn(List.of(installment));

        when(repository.findByLoanIdAndIsPaidIsOrderByCreatedTimeAsc(1L, false))
                .thenReturn(List.of(installment));

        try (MockedStatic<UserContextHolder> mockedStatic = mockStatic(UserContextHolder.class)) {
            mockedStatic.when(UserContextHolder::getUser).thenReturn(user);

            ResponseEntity<LoanPaymentResultDTO> response = loanInstallmentService.payLoanInstallment(paymentDTO);

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }
    }
}
