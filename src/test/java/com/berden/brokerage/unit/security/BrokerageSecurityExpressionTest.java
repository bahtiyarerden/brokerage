package com.berden.brokerage.unit.security;

import com.berden.brokerage.security.BrokerageSecurityExpression;
import com.berden.brokerage.security.UserPrincipal;
import com.berden.brokerage.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BrokerageSecurityExpressionTest {

    private final Long TEST_CUSTOMER_ID = 123L;
    private final Long DIFFERENT_CUSTOMER_ID = 456L;
    private final Long TEST_ORDER_ID = 789L;
    @Mock
    private OrderService orderService;
    @Mock
    private SecurityContext securityContext;
    @Mock
    private Authentication authentication;
    @InjectMocks
    private BrokerageSecurityExpression securityExpression;
    private UserPrincipal testUser;

    @BeforeEach
    public void setup() {
        Long TEST_USER_ID = 1L;
        testUser = new UserPrincipal(
                TEST_USER_ID,
                "testuser",
                "password",
                Collections.singleton(new SimpleGrantedAuthority("ROLE_CUSTOMER")),
                TEST_CUSTOMER_ID
        );

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void isResourceOwner_WhenUserIsResourceOwner_ReturnsTrue() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);

        boolean result = securityExpression.isResourceOwner(TEST_CUSTOMER_ID);

        assertTrue(result);
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
    }

    @Test
    public void isResourceOwner_WhenUserIsNotResourceOwner_ReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);

        boolean result = securityExpression.isResourceOwner(DIFFERENT_CUSTOMER_ID);

        assertFalse(result);
        verify(securityContext).getAuthentication();
        verify(authentication).getPrincipal();
    }

    @Test
    public void isResourceOwner_WhenAuthenticationIsNull_ReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(null);

        boolean result = securityExpression.isResourceOwner(TEST_CUSTOMER_ID);

        assertFalse(result);
        verify(securityContext).getAuthentication();
        verifyNoInteractions(authentication);
    }

    @Test
    public void isResourceOwnerByOrderId_WhenUserIsOrderOwner_ReturnsTrue() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(orderService.getCustomerIdByOrderId(TEST_ORDER_ID)).thenReturn(TEST_CUSTOMER_ID);

        boolean result = securityExpression.isResourceOwnerByOrderId(TEST_ORDER_ID);

        assertTrue(result);
        verify(orderService).getCustomerIdByOrderId(TEST_ORDER_ID);
    }

    @Test
    public void isResourceOwnerByOrderId_WhenUserIsNotOrderOwner_ReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(orderService.getCustomerIdByOrderId(TEST_ORDER_ID)).thenReturn(DIFFERENT_CUSTOMER_ID);

        boolean result = securityExpression.isResourceOwnerByOrderId(TEST_ORDER_ID);

        assertFalse(result);
        verify(orderService).getCustomerIdByOrderId(TEST_ORDER_ID);
    }

    @Test
    public void isResourceOwnerByOrderId_WhenOrderServiceThrowsException_ReturnsFalse() {
        when(orderService.getCustomerIdByOrderId(TEST_ORDER_ID))
                .thenThrow(new RuntimeException("Order not found"));

        boolean result = securityExpression.isResourceOwnerByOrderId(TEST_ORDER_ID);

        assertFalse(result);
        verify(orderService).getCustomerIdByOrderId(TEST_ORDER_ID);
    }

    @Test
    public void isResourceOwnerByOrderId_WhenAuthenticationIsNotInSecurityContextHolder_ReturnsFalse() {
        when(securityContext.getAuthentication()).thenReturn(null);
        when(orderService.getCustomerIdByOrderId(TEST_ORDER_ID)).thenReturn(TEST_CUSTOMER_ID);

        boolean result = securityExpression.isResourceOwnerByOrderId(TEST_ORDER_ID);

        assertFalse(result);
        verify(orderService).getCustomerIdByOrderId(TEST_ORDER_ID);
    }

}