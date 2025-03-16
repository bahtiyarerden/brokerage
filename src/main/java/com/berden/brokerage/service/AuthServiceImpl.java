package com.berden.brokerage.service;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.dto.external.request.LoginRequest;
import com.berden.brokerage.dto.external.response.LoginResponse;
import com.berden.brokerage.dto.internal.UserDTO;
import com.berden.brokerage.entity.User;
import com.berden.brokerage.exception.DuplicateResourceException;
import com.berden.brokerage.repository.UserRepository;
import com.berden.brokerage.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Override
    public LoginResponse login(LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("ROLE_" + Role.CUSTOMER);


            String token = jwtTokenProvider.generateToken(authentication);
            return new LoginResponse(token, role);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("invalid credentials");
        }
    }

    @Override
    @PreAuthorize("hasRole('ADMIN')")
    public UserDTO registerUser(String username, String password, Role role, Long customerId) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new DuplicateResourceException("user already exists with username: " + username);
        }

        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .role(role)
                .customerId(customerId)
                .build();

        User savedUser = userRepository.save(user);
        return UserDTO.fromEntity(savedUser);

    }
}
