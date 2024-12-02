package com.ing.credit_module.authentication;

import com.ing.credit_module.dto.LoginDTO;
import com.ing.credit_module.dto.RefreshTokenDTO;
import com.ing.credit_module.dto.SignUpDTO;
import com.ing.credit_module.exception.InvalidTokenHttpException;
import com.ing.credit_module.exception.UserAlreadyExistsHttpException;
import com.ing.credit_module.exception.UserNotFoundHttpException;

public interface IAuthService {
    Tokens register(SignUpDTO signUpDTO) throws UserAlreadyExistsHttpException;

    Tokens login(LoginDTO loginDTO) throws UserNotFoundHttpException;

    Tokens refreshToken(RefreshTokenDTO refreshTokenDTO) throws InvalidTokenHttpException;
}
