package com.tnc.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class InternalTokenAuthorizationFilterTest {

    private InternalTokenService internalTokenService;
    private InternalTokenAuthorizationFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        internalTokenService = mock(InternalTokenService.class);
        filter = new InternalTokenAuthorizationFilter(internalTokenService);
    }

    @Test
    void should_set_authentication_when_token_valid() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-Internal-Token")).thenReturn("valid-token");
        when(internalTokenService.validateInternalToken("valid-token")).thenReturn(true);
        when(internalTokenService.getUsernameFromInternalToken("valid-token")).thenReturn("svc");
        when(internalTokenService.getAuthoritiesFromInternalToken("valid-token")).thenReturn("ROLE_INTERNAL,ROLE_SYSTEM");

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getName()).isEqualTo("svc");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities()).extracting("authority")
                .containsExactlyInAnyOrder("ROLE_INTERNAL", "ROLE_SYSTEM");

        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void should_clear_context_when_token_invalid() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getMethod()).thenReturn("GET");
        when(request.getHeader("X-Internal-Token")).thenReturn("bad-token");
        when(internalTokenService.validateInternalToken("bad-token")).thenReturn(false);

        filter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(chain, times(1)).doFilter(request, response);
    }

    @Test
    void should_return_ok_for_options() throws ServletException, IOException {
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain chain = mock(FilterChain.class);

        when(request.getMethod()).thenReturn("OPTIONS");

        filter.doFilterInternal(request, response, chain);

        verify(response).setStatus(HttpServletResponse.SC_OK);
        verify(chain, times(1)).doFilter(request, response);
    }
}


