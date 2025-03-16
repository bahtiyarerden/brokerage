package com.berden.brokerage.security;

import com.berden.brokerage.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class UserPrincipal implements UserDetails {

    private Long id;
    private String username;

    @JsonIgnore
    private String password;

    private Collection<? extends GrantedAuthority> authorities;

    private Long customerId;

    public UserPrincipal(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities, Long customerId) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.authorities = authorities;
        this.customerId = customerId;
    }

    public static UserPrincipal create(User user) {
        Set<SimpleGrantedAuthority> authoritySet = Collections.singleton(
                new SimpleGrantedAuthority("ROLE_" + user.getRole()));

        return new UserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authoritySet,
                user.getCustomerId()
        );
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public Long getId() {
        return id;
    }

    public Long getCustomerId() {
        return customerId;
    }

}
