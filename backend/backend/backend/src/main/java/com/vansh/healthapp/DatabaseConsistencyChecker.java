package com.vansh.healthapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
public class DatabaseConsistencyChecker implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("Running database consistency check...");
        
        // Check for inconsistent doctor-user relationships
        List<Map<String, Object>> orphanedDoctors = jdbcTemplate.queryForList(
            "SELECT d.id, d.user_id FROM doctors d LEFT JOIN users u ON d.user_id = u.id WHERE u.id IS NULL"
        );
        
        if (!orphanedDoctors.isEmpty()) {
            System.out.println("Found " + orphanedDoctors.size() + " doctors with invalid user references");
            
            // Option 1: Remove the orphaned doctors
            jdbcTemplate.update("DELETE FROM doctors WHERE id IN (SELECT d.id FROM doctors d LEFT JOIN users u ON d.user_id = u.id WHERE u.id IS NULL)");
            
            // Option 2 (alternative): Create missing users for orphaned doctors
            // This would require creating a user for each orphaned doctor
            
            System.out.println("Fixed doctors table by removing orphaned records");
        } else {
            System.out.println("No database inconsistencies found in doctors table");
        }
        
        // Check for other potential inconsistencies in patients table
        List<Map<String, Object>> orphanedPatients = jdbcTemplate.queryForList(
            "SELECT p.id, p.user_id FROM patients p LEFT JOIN users u ON p.user_id = u.id WHERE u.id IS NULL"
        );
        
        if (!orphanedPatients.isEmpty()) {
            System.out.println("Found " + orphanedPatients.size() + " patients with invalid user references");
            jdbcTemplate.update("DELETE FROM patients WHERE id IN (SELECT p.id FROM patients p LEFT JOIN users u ON p.user_id = u.id WHERE u.id IS NULL)");
            System.out.println("Fixed patients table by removing orphaned records");
        } else {
            System.out.println("No database inconsistencies found in patients table");
        }
        
        System.out.println("Database consistency check completed");
    }
} 