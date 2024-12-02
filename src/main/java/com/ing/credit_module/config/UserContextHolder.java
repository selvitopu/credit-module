package com.ing.credit_module.config;

import com.ing.credit_module.authentication.CreditUserDetailsService;
import com.ing.credit_module.model.User;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserContextHolder {
    private UserContextHolder() {
    }

    public static User getUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        CreditUserDetailsService.CreditUserDetails userDetails = (CreditUserDetailsService.CreditUserDetails) principal;
        return userDetails.getUser();
    }
}
