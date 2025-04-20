package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.DoctorAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface DoctorAvailabilityRepository extends JpaRepository<DoctorAvailability, Long> {

    List<DoctorAvailability> findByDoctor(Doctor doctor);
    
    List<DoctorAvailability> findByDoctorAndDate(Doctor doctor, LocalDate date);
    
    List<DoctorAvailability> findByDoctorAndDateBetween(Doctor doctor, LocalDate startDate, LocalDate endDate);
    
    List<DoctorAvailability> findByDate(LocalDate date);
    
    List<DoctorAvailability> findByDoctorAndAvailable(Doctor doctor, boolean available);
    
    List<DoctorAvailability> findByDoctorAndDateAndAvailable(Doctor doctor, LocalDate date, boolean available);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.doctor = :doctor " +
           "AND da.date = :date AND da.available = true " +
           "AND da.startTime <= :time AND da.endTime >= :time")
    List<DoctorAvailability> findAvailabilityForDoctorAtDateTime(
            @Param("doctor") Doctor doctor,
            @Param("date") LocalDate date,
            @Param("time") LocalTime time);
    
    @Query("SELECT da FROM DoctorAvailability da WHERE da.date = :date " +
           "AND da.available = true")
    List<DoctorAvailability> findAvailableSlotsByDate(@Param("date") LocalDate date);
} 