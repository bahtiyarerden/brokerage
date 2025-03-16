package com.berden.brokerage.unit.repository;

import com.berden.brokerage.common.enums.Role;
import com.berden.brokerage.entity.User;
import com.berden.brokerage.helpers.UserTestHelper;
import com.berden.brokerage.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    
    @Test
    public void testSaveUserAndFindById() {
        User user = UserTestHelper.createUserAdmin();
        User savedUser = userRepository.save(user);

        Optional<User> maybeUser = userRepository.findById(savedUser.getId());


        assertTrue(maybeUser.isPresent());
        assertEquals("user", maybeUser.get().getUsername());
    }


    @Test
    public void testUniqueUsernameConstraint() {
        User user = UserTestHelper.createUserAdmin();
        userRepository.save(user);

        User duplicateUser = User.builder()
                .username(user.getUsername())
                .password("anotherpassword")
                .role(Role.ADMIN)
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepository.save(duplicateUser);
        });
    }

    @Test
    public void testFindByUsername() {
        User user = UserTestHelper.createUserAdmin();
        userRepository.save(user);

        Optional<User> foundUser = userRepository.findByUsername("user");

        assertTrue(foundUser.isPresent());
        assertEquals("user", foundUser.get().getUsername());
    }
}
