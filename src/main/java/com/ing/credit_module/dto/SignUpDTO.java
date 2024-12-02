/*
 * Copyright (c) 2020.
 */

package com.ing.credit_module.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpDTO {

    private String name;

    private String surname;

    private String username;

    private String password;

    private String confirmPassword;

    private BigDecimal creditLimit;

}
