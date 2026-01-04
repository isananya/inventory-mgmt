package com.chubb.inventoryapp.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_NoCookies_ShouldContinueChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(any());
    }

    @Test
    void doFilterInternal_CookiesPresentButNoJwtToken_ShouldContinueChain() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("other_cookie", "some_value"));
        MockHttpServletResponse response = new MockHttpServletResponse();

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractUsername(any());
    }

    @Test
    void doFilterInternal_ValidToken_ShouldAuthenticateUser() throws ServletException, IOException {
        String token = "valid.jwt.token";
        String username = "user@test.com";
        String role = "CUSTOMER";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt_token", token));
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = mock(UserDetails.class);

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(jwtService.extractRole(token)).thenReturn(role);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        
        String authority = SecurityContextHolder.getContext().getAuthentication().getAuthorities().iterator().next().getAuthority();
        assertEquals("CUSTOMER", authority);

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_InvalidToken_ShouldNotAuthenticate() throws ServletException, IOException {
        String token = "invalid.jwt.token";
        String username = "user@test.com";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt_token", token));
        MockHttpServletResponse response = new MockHttpServletResponse();

        UserDetails userDetails = mock(UserDetails.class);

        when(jwtService.extractUsername(token)).thenReturn(username);
        when(userDetailsService.loadUserByUsername(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        
        verify(filterChain).doFilter(request, response);
        verify(jwtService, never()).extractRole(any());
    }

    @Test
    void doFilterInternal_AuthAlreadyPresent_ShouldSkipLogic() throws ServletException, IOException {
        String token = "some.jwt.token";
        
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("jwt_token", token));
        MockHttpServletResponse response = new MockHttpServletResponse();

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken("existingUser", null)
        );

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(jwtService, never()).extractUsername(any());
        verify(userDetailsService, never()).loadUserByUsername(any());
        
        verify(filterChain).doFilter(request, response);
    }
}