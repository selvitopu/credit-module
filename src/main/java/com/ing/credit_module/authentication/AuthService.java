

/*
 * Copyright (c) 2020.
 */

package com.ing.credit_module.authentication;

import com.ing.credit_module.exception.InvalidTokenHttpException;
import com.ing.credit_module.exception.UserAlreadyExistsHttpException;
import com.ing.credit_module.exception.UserNotFoundHttpException;
import com.ing.credit_module.dto.LoginDTO;
import com.ing.credit_module.dto.RefreshTokenDTO;
import com.ing.credit_module.dto.SignUpDTO;
import com.ing.credit_module.exception.UserAlreadyExistsException;
import com.ing.credit_module.exception.UserNotFoundException;
import com.ing.credit_module.model.User;
import com.ing.credit_module.service.IUserService;
import io.jsonwebtoken.JwtException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements IAuthService {

    private IUserService userService;
    private AuthenticationManager authenticationManager;
    private TokenService tokenService;

    @Lazy
    @Autowired
    public AuthService(IUserService userService, AuthenticationManager authenticationManager, TokenService tokenService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    @Override
    public Tokens register(SignUpDTO signUpDTO) throws UserAlreadyExistsHttpException {
        try {
            User user = userService.register(signUpDTO);

            return createToken(user);
        } catch (UserAlreadyExistsException exception) {
            throw new UserAlreadyExistsHttpException();
        }
    }

    @Override
    public Tokens login(LoginDTO loginDTO) throws UserNotFoundHttpException {
        try {
            Authentication authentication = createAuthentication(loginDTO);
            CreditUserDetailsService.CreditUserDetails userDetails =
                    (CreditUserDetailsService.CreditUserDetails) authenticationManager
                            .authenticate(authentication).getPrincipal();
            User user = userDetails.getUser();

            return createToken(user);
        } catch (AuthenticationException exception) {
            throw new UserNotFoundHttpException("Incorrect email or password", HttpStatus.FORBIDDEN);
        }
    }

    @Override
    public Tokens refreshToken(RefreshTokenDTO refreshTokenDTO) throws InvalidTokenHttpException {
        try {
            String username = tokenService.getUsernameFromRefreshToken(refreshTokenDTO.getTokens().getRefreshToken());
            User user = userService.findByUsername(username);
            return createToken(user);
        } catch (JwtException | UserNotFoundException e) {
            throw new InvalidTokenHttpException();
        }
    }

    private Authentication createAuthentication(LoginDTO loginDTO) {
        return new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword());
    }

    private Tokens createToken(User user) {
        return tokenService.createToken(user);
    }

}
