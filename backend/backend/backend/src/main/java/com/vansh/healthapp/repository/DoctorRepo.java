package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
    Optional<Doctor> findByUserEmail(String email);
} 