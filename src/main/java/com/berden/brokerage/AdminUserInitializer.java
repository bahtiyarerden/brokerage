package com.berden.brokerage;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.entity.User;
import com.berden.brokerage.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserInitializer implements CommandLineRunner {
    private final Logger logger = LoggerFactory.getLogger(AdminUserInitializer.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    public void run(String... args) throws Exception {
        final String username = "admin";
        final String password = "admin";
        if (userRepository.findByUsername(username).isEmpty()) {
            User admin = new User();
            admin.setUsername(username);
            admin.setPassword(passwordEncoder.encode(password));
            admin.setRole(Role.ADMIN);

            userRepository.save(admin);
            logger.info("admin user created for testing purposes with username: {} password: {}", username, password);
        }
    }
}