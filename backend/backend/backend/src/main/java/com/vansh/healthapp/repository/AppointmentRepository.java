package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.Appointment;
import com.vansh.healthapp.model.Doctor;
import com.vansh.healthapp.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByAppointmentNumber(String appointmentNumber);
    
    List<Appointment> findByPatientOrderByAppointmentDateDescStartTimeDesc(Patient patient);
    
    List<Appointment> findByDoctorOrderByAppointmentDateDescStartTimeDesc(Doctor doctor);
    
    List<Appointment> findByPatientAndStatusOrderByAppointmentDateDescStartTimeDesc(
            Patient patient, Appointment.AppointmentStatus status);
    
    List<Appointment> findByDoctorAndStatusOrderByAppointmentDateDescStartTimeDesc(
            Doctor doctor, Appointment.AppointmentStatus status);
    
    List<Appointment> findByAppointmentDateAndDoctor(LocalDate date, Doctor doctor);
    
    List<Appointment> findByAppointmentDateAndPatient(LocalDate date, Patient patient);
    
    List<Appointment> findByAppointmentDateBetweenAndDoctor(
            LocalDate startDate, LocalDate endDate, Doctor doctor);
    
    List<Appointment> findByAppointmentDateBetweenAndPatient(
            LocalDate startDate, LocalDate endDate, Patient patient);
    
    List<Appointment> findByAppointmentDateAndStatusIn(
            LocalDate date, List<Appointment.AppointmentStatus> statuses);
    
    @Query("SELECT a FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate = :date " +
           "AND a.status != 'CANCELLED' AND ((a.startTime <= :endTime AND a.endTime >= :startTime))")
    List<Appointment> findOverlappingAppointments(
            @Param("doctor") Doctor doctor,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.doctor = :doctor AND a.appointmentDate = :date AND a.status = 'CONFIRMED'")
    Long countConfirmedAppointmentsForDoctorOnDate(
            @Param("doctor") Doctor doctor,
            @Param("date") LocalDate date);
    
    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.patient = :patient AND a.status = 'NO_SHOW'")
    Long countNoShowAppointmentsForPatient(@Param("patient") Patient patient);
} 