package com.vansh.healthapp.config;

import com.vansh.healthapp.model.Role;
import com.vansh.healthapp.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DatabaseInitializer(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public void run(String... args) {
        // Create default roles if they don't exist
        if (roleRepository.count() == 0) {
            List<Role> roles = Arrays.asList(
                    new Role(null, "ADMIN", "Administrator with full access"),
                    new Role(null, "DOCTOR", "Doctor role for medical professionals"),
                    new Role(null, "PATIENT", "Patient role for healthcare recipients"),
                    new Role(null, "STAFF", "Staff role for healthcare facility personnel")
            );
            
            roleRepository.saveAll(roles);
            System.out.println("Default roles have been created");
        }
    }
} 