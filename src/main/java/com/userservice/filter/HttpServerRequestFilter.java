package com.userservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
public class HttpServerRequestFilter extends OncePerRequestFilter {

    private static final String X_CORRELATION_ID = "x-correlation-id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            MDC.put(X_CORRELATION_ID, Optional.ofNullable(request.getHeader(X_CORRELATION_ID))
                    .orElseGet(() -> UUID.randomUUID().toString()));
            response.setHeader(X_CORRELATION_ID, MDC.get(X_CORRELATION_ID));


            filterChain.doFilter(request, response);

        } finally {
            MDC.clear();
        }
    }
}

