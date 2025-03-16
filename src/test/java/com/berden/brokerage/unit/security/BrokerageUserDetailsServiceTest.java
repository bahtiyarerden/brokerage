package com.berden.brokerage.unit.security;

import com.berden.brokerage.entity.User;
import com.berden.brokerage.helpers.UserTestHelper;
import com.berden.brokerage.repository.UserRepository;
import com.berden.brokerage.security.BrokerageUserDetailsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BrokerageUserDetailsServiceTest {
    @InjectMocks
    private BrokerageUserDetailsService userDetailsService;

    @Mock
    private UserRepository userRepository;

    @Test
    void loadUserByUsername_WhenUserFound_ShouldReturnUserDetails() {
        User userCustomer = UserTestHelper.createUserCustomer();
        String username = userCustomer.getUsername();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(userCustomer));

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
    }

    @Test
    void loadUserByUsername_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername("username");
        });
    }
}
