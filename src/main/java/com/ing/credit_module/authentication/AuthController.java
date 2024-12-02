

/*
 * Copyright (c) 2020.
 */

package com.ing.credit_module.authentication;

import com.ing.credit_module.exception.PasswordsDontMatchException;
import com.ing.credit_module.dto.LoginDTO;
import com.ing.credit_module.dto.RefreshTokenDTO;
import com.ing.credit_module.dto.SignUpDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import static org.springframework.http.ResponseEntity.ok;


@Controller
@RequestMapping("/auth")
public class AuthController {

    private IAuthService authService;

    @Lazy
    @Autowired
    public AuthController(IAuthService authService) {
        this.authService = authService;
    }

    /**
     * Login user
     *
     * @param loginDTO user credentials
     * @return generated token
     */
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody LoginDTO loginDTO) {
        Tokens tokens = authService.login(loginDTO);
        return toResponse(tokens);
    }

    /**
     * Sign up
     *
     * @param signUpDTO sign up user data
     * @return token
     */
    @PostMapping("/sign-up")
    public ResponseEntity register(@RequestBody SignUpDTO signUpDTO) {
        if (!signUpDTO.getPassword().equals(signUpDTO.getConfirmPassword())) {
            throw new PasswordsDontMatchException();
        }

        Tokens tokens = authService.register(signUpDTO);
        return toResponse(tokens);
    }

    /**
     * Sign out. Perform any required actions to log out user, like invalidate user session.
     * Implement your required logic
     *
     * @return result message
     */
    @PostMapping("/sign-out")
    public ResponseEntity logout() {
        return ResponseEntity.ok().build();
    }

    /**
     * Refresh token
     *
     * @param refreshTokenDTO refresh token
     * @return new token
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenDTO> refreshToken(@RequestBody RefreshTokenDTO refreshTokenDTO) {
        Tokens tokens = authService.refreshToken(refreshTokenDTO);
        return toResponse(tokens);
    }

    private ResponseEntity<RefreshTokenDTO> toResponse(Tokens tokens) {
        return ok(new RefreshTokenDTO(tokens));
    }
}
