package com.berden.brokerage.unit.repository;

import com.berden.brokerage.entity.Customer;
import com.berden.brokerage.helpers.CustomerTestHelper;
import com.berden.brokerage.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testSaveCustomerAndFindById() {
        Customer customer = CustomerTestHelper.createCustomer();
        Customer savedCustomer = customerRepository.save(customer);
        Optional<Customer> maybeCustomer = customerRepository.findById(savedCustomer.getId());

        assertTrue(maybeCustomer.isPresent());
        assertEquals("test@test.com", maybeCustomer.get().getEmail());
    }

    @Test
    public void testFindByEmail() {
        Customer customer = CustomerTestHelper.createCustomer();
        customerRepository.save(customer);

        Optional<Customer> foundCustomer = customerRepository.findByEmail("test@test.com");

        assertTrue(foundCustomer.isPresent());
        assertEquals("test@test.com", foundCustomer.get().getEmail());
    }

    @Test
    public void testUniqueEmailConstraint() {
        Customer customer = CustomerTestHelper.createCustomer();
        customerRepository.save(customer);

        Customer duplicateCustomer = Customer.builder()
                .email(customer.getEmail())
                .build();

        assertThrows(DataIntegrityViolationException.class, () -> {
            customerRepository.save(duplicateCustomer);
        });
    }
}
