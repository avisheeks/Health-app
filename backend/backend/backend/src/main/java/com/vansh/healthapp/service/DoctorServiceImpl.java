package com.vansh.healthapp.service;

import com.vansh.healthapp.exception.ResourceNotFoundException;
import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.User;
import com.vansh.healthapp.repository.DoctorRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepo doctorRepository;

    public DoctorServiceImpl(DoctorRepo doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    @Override
    public Doctor getDoctorById(Long id) {
        return doctorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with id: " + id));
    }

    @Override
    public Doctor getDoctorByUser(User user) {
        return doctorRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found for user: " + user.getEmail()));
    }

    @Override
    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found with email: " + email));
    }

    @Override
    @Transactional
    public Doctor createDoctor(User user) {
        Doctor doctor = new Doctor();
        doctor.setUser(user);
        doctor.setStatus("ACTIVE");
        doctor.setReceivedAt(LocalDateTime.now());
        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public Doctor updateDoctor(Long id, Doctor doctorDetails) {
        Doctor doctor = getDoctorById(id);
        doctor.setStatus(doctorDetails.getStatus());
        return doctorRepository.save(doctor);
    }

    @Override
    @Transactional
    public void deleteDoctor(Long id) {
        Doctor doctor = getDoctorById(id);
        doctorRepository.delete(doctor);
    }
} 