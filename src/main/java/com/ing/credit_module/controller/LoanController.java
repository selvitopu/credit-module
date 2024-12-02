package com.ing.credit_module.controller;


import com.ing.credit_module.dto.LoanDTO;
import com.ing.credit_module.service.ILoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.ing.credit_module.constants.ControllerConstants.LOAN_CONTROLLER;

@RestController
@RequiredArgsConstructor
@RequestMapping(LOAN_CONTROLLER)
public class LoanController {

    private final ILoanService loanService;

    @GetMapping("")
    public ResponseEntity<List<LoanDTO>> getLoansByUser(@RequestParam boolean isPaid,
                                                        @RequestParam Integer numberOfInstallments) {
        return loanService.getLoansByUser(isPaid, numberOfInstallments);
    }

    @PostMapping("")
    public ResponseEntity<LoanDTO> createLoan(@RequestBody LoanDTO loanDTO) {
        return loanService.createLoan(loanDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity deleteLoan(@PathVariable Long id) {
        return loanService.deleteLoan(id);
    }

}
