package com.userservice.security.jwt;

import com.userservice.exception.JwtTokenInvalidException;
import com.userservice.exception.JwtTokenMissingException;
import com.userservice.model.entity.UserEntity;
import com.userservice.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(String url, AuthenticationManager authenticationManager, JwtTokenUtil jwtTokenUtil, UserRepository userRepository) {
        super(new AntPathRequestMatcher(url));
        this.userRepository = userRepository;
        setAuthenticationManager(authenticationManager);
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) {
        String token = getTokenFromHeader(request);
        validateJwtToken(token);

        if (token == null) {
            throw new JwtTokenInvalidException("Invalid or missing JWT token");
        }

        String username = jwtTokenUtil.extractUsername(token);
        if (username == null) {
            throw new JwtTokenInvalidException("JWT token does not contain a valid username. token:  "+ token);
        }

        UserEntity user = userRepository.findUserByEmail(username)
                .orElseThrow(() -> new JwtTokenInvalidException("User from within token not found with email: " + username + ". and token: " + token));

        return new UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
    }


    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        SecurityContextHolder.getContext().setAuthentication(authResult);
        chain.doFilter(request, response);
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        log.info("getTokenFromHeader() Jwt: " + bearerToken);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    private void validateJwtToken(String token) throws JwtTokenMissingException, JwtTokenInvalidException {
        log.info("Extracted Username: " + jwtTokenUtil.extractUsername(token));
        if (!jwtTokenUtil.validateToken(token, userRepository.findByEmail(jwtTokenUtil.extractUsername(token)))) {
            throw new JwtTokenInvalidException("Invalid JWT token. Token: " + token);
        }
    }
}
