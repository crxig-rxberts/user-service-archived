package com.userservice.filter;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@Slf4j
class HttpServerRequestFilterTest {

    @InjectMocks
    private HttpServerRequestFilter httpServerRequestFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    @BeforeEach
    void init() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @Test
    void whenCorrelationIdIsPresentItPersistsDownStream() throws ServletException, IOException {
        String correlationId = UUID.randomUUID().toString();
        request.addHeader("x-correlation-id", correlationId);

        httpServerRequestFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("x-correlation-id")).isEqualTo(correlationId);
    }

    @Test
    void whenNoCorrelationIdIsPresentNewIdIsGenerated() throws ServletException, IOException {

        httpServerRequestFilter.doFilterInternal(request, response, filterChain);

        assertThat(response.getHeader("x-correlation-id")).isNotNull();
    }
}

