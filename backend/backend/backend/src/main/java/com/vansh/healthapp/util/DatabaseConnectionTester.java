package com.vansh.healthapp.util;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class DatabaseConnectionTester {

    @Bean
    public CommandLineRunner testDatabaseConnection(JdbcTemplate jdbcTemplate) {
        return args -> {
            try {
                System.out.println("=========== TESTING DATABASE CONNECTION ===========");
                String result = jdbcTemplate.queryForObject("SELECT 'Connection successful!'", String.class);
                System.out.println(result);
                System.out.println("====================================================");
            } catch (Exception e) {
                System.err.println("=========== DATABASE CONNECTION FAILED ===========");
                System.err.println("Error: " + e.getMessage());
                System.err.println("====================================================");
                e.printStackTrace();
            }
        };
    }
} 