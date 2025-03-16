package com.berden.brokerage.unit.service;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.dto.external.request.LoginRequest;
import com.berden.brokerage.dto.external.response.LoginResponse;
import com.berden.brokerage.dto.internal.UserDTO;
import com.berden.brokerage.entity.User;
import com.berden.brokerage.exception.DuplicateResourceException;
import com.berden.brokerage.helpers.UserTestHelper;
import com.berden.brokerage.repository.UserRepository;
import com.berden.brokerage.security.JwtTokenProvider;
import com.berden.brokerage.service.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthServiceImpl authService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @Test
    public void login_WhenCredentialsAreValid_ShouldReturnTokenAndRole() {

        LoginRequest request = new LoginRequest("testuser", "password");
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER"));

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getAuthorities()).thenReturn((Collection) authorities);
        when(jwtTokenProvider.generateToken(authentication)).thenReturn("mocked-jwt-token");

        LoginResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mocked-jwt-token", response.token());
        assertEquals("ROLE_CUSTOMER", response.role());

        verify(authenticationManager).authenticate(any());
        verify(authentication).getPrincipal();
        verify(userDetails).getAuthorities();
        verify(jwtTokenProvider).generateToken(authentication);
    }

    @Test
    public void login_WhenCredentialsAreInvalid_ShouldThrowException() {
        LoginRequest request = new LoginRequest("testuser", "wrongpassword");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        assertThrows(BadCredentialsException.class, () -> authService.login(request));

        verify(authenticationManager).authenticate(any());
        verify(userDetails, never()).getAuthorities();
        verify(jwtTokenProvider, never()).generateToken(any());
    }

    @Test
    public void registerUser_WhenUsernameIsAvailable_ShouldCreateUser() {
        User user = UserTestHelper.createUserCustomer();
        String rawPassword = "customerstrongpass";
        String encodedPassword = "encoded-password";

        when(userRepository.findByUsername("customer")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(userRepository.save(any())).thenReturn(user);

        UserDTO registeredUser = authService.registerUser("customer", rawPassword, Role.CUSTOMER, 1L);

        assertNotNull(registeredUser);
        assertEquals("customer", registeredUser.username());
        assertEquals(Role.CUSTOMER, registeredUser.role());

        verify(userRepository).findByUsername("customer");
        verify(passwordEncoder).encode(rawPassword);
        verify(userRepository).save(any());
    }

    @Test
    public void registerUser_WhenUsernameAlreadyExists_ShouldThrowDuplicateResourceException() {
        User user = UserTestHelper.createUserCustomer();

        when(userRepository.findByUsername("customer")).thenReturn(Optional.of(user));

        assertThrows(DuplicateResourceException.class, () ->
                authService.registerUser("customer", "anotherpass", Role.CUSTOMER, 2L)
        );

        verify(userRepository).findByUsername("customer");
        verify(userRepository, never()).save(any());
    }
}
