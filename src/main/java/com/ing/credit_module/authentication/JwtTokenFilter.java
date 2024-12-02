package com.ing.credit_module.authentication;

import com.ing.credit_module.exception.AuthenticationException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public JwtTokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        if (path.equals("/auth/login") || path.equals("/auth/sign-up") || path.equals("/auth/refresh-token")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            AuthenticationToken token = tokenService.resolveToken(request);

            if (token != null) {
                SecurityContextHolder.getContext().setAuthentication(tokenService.getAuthentication(token));
            }
        } catch (AuthenticationException e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Unauthorized: Invalid or missing token");
            response.getWriter().flush();
            return;
        }

        filterChain.doFilter(request, response);
    }
}