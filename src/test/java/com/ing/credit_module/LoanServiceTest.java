package com.ing.credit_module;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ing.credit_module.authentication.CreditUserDetailsService;
import com.ing.credit_module.config.UserContextHolder;
import com.ing.credit_module.dto.LoanDTO;
import com.ing.credit_module.exception.CreditBusinessRuleException;
import com.ing.credit_module.model.Loan;
import com.ing.credit_module.model.User;
import com.ing.credit_module.repository.ILoanRepository;
import com.ing.credit_module.service.IUserService;
import com.ing.credit_module.service.impl.LoanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LoanServiceTest {
    @Mock
    private ILoanRepository loanRepository;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private IUserService userService;

    @InjectMocks
    private LoanService loanService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @BeforeEach
    void setUpSecurityContext() {
        User mockUser = new User();
        mockUser.setUsername("testUser");
        mockUser.setPasswordHash("password");

        CreditUserDetailsService.CreditUserDetails principal = new CreditUserDetailsService.CreditUserDetails(mockUser, List.of());

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    void testGetLoansByUser_UserNotFound() {
        try (MockedStatic<UserContextHolder> mockedStatic = Mockito.mockStatic(UserContextHolder.class)) {
            mockedStatic.when(UserContextHolder::getUser).thenReturn(null);

            ResponseEntity<List<LoanDTO>> response = loanService.getLoansByUser(false, 12);

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }
    }

    @Test
    void testFindLoansByCustomer() {
        User user = new User();
        Loan loan = new Loan();
        when(loanRepository.findLoansByUser(user)).thenReturn(List.of(loan));

        List<Loan> loans = loanService.findLoansByCustomer(user);

        assertEquals(1, loans.size());
        verify(loanRepository, times(1)).findLoansByUser(user);
    }

    @Test
    void testGetLoansByUser_NoLoansFound() {
        User user = new User();
        try (MockedStatic<UserContextHolder> mockedStatic = Mockito.mockStatic(UserContextHolder.class)) {

            when(UserContextHolder.getUser()).thenReturn(user);
            when(loanRepository.findLoansByUserAndNumberOfInstallmentsAndIsPaid(user, 12, false)).thenReturn(List.of());

            ResponseEntity<List<LoanDTO>> response = loanService.getLoansByUser(false, 12);

            assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        }
    }

    @Test
    void testGetLoansByUser_Success() {
        User user = new User();
        Loan loan = new Loan();
        LoanDTO loanDTO = new LoanDTO();

        try (MockedStatic<UserContextHolder> mockedStatic = Mockito.mockStatic(UserContextHolder.class)) {

            when(UserContextHolder.getUser()).thenReturn(user);
            when(loanRepository.findLoansByUserAndNumberOfInstallmentsAndIsPaid(user, 12, false)).thenReturn(List.of(loan));
            when(objectMapper.convertValue(loan, LoanDTO.class)).thenReturn(loanDTO);

            ResponseEntity<List<LoanDTO>> response = loanService.getLoansByUser(false, 12);

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(1, response.getBody().size());
        }
    }

    @Test
    void testCreateLoan_UserLimitExceeded() {
        LoanDTO loanDTO = new LoanDTO();
        Loan loan = new Loan();
        User user = new User();
        user.setCreditLimit(new BigDecimal("2000"));
        loan.setAmount(new BigDecimal("2400"));
        loan.setInterestRate(0.1);
        loan.setNumberOfInstallments(12);


        try (MockedStatic<UserContextHolder> mockedStatic = Mockito.mockStatic(UserContextHolder.class)) {
            mockedStatic.when(UserContextHolder::getUser).thenReturn(user);

            when(UserContextHolder.getUser()).thenReturn(user);
            when(objectMapper.convertValue(loanDTO, Loan.class)).thenReturn(loan);

            assertThrows(CreditBusinessRuleException.class, () -> loanService.createLoan(loanDTO));
        }
    }

    @Test
    void testCreateLoan_Success() {
        LoanDTO loanDTO = new LoanDTO();
        Loan loan = new Loan();
        User user = new User();
        user.setCreditLimit(new BigDecimal("2000"));
        loan.setAmount(new BigDecimal("1200"));
        loan.setInterestRate(0.1);
        loan.setNumberOfInstallments(12);

        try (MockedStatic<UserContextHolder> mockedStatic = Mockito.mockStatic(UserContextHolder.class)) {
            mockedStatic.when(UserContextHolder::getUser).thenReturn(user);

            when(objectMapper.convertValue(loanDTO, Loan.class)).thenReturn(loan);
            when(loanRepository.save(loan)).thenReturn(loan);
            when(objectMapper.convertValue(loan, LoanDTO.class)).thenReturn(loanDTO);

            ResponseEntity<LoanDTO> response = loanService.createLoan(loanDTO);

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            verify(userService, times(1)).saveUser(user);
            verify(loanRepository, times(1)).save(loan);
        }
    }

    @Test
    void testDeleteLoan_NotFound() {
        when(loanRepository.findById(1L)).thenReturn(Optional.empty());

        ResponseEntity<String> response = loanService.deleteLoan(1L);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteLoan_Success() {
        Loan loan = new Loan();
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

        ResponseEntity<String> response = loanService.deleteLoan(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(loanRepository, times(1)).delete(loan);
    }
}
