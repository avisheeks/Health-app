package com.vansh.healthapp.repository;

import com.vansh.healthapp.model.LoginSessionEntity;
import com.vansh.healthapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoginSessionRepository extends JpaRepository<LoginSessionEntity, Long> {
    List<LoginSessionEntity> findByUser(User user);
    
    List<LoginSessionEntity> findByUserAndLoginTimeBetween(User user, LocalDateTime start, LocalDateTime end);
    
    List<LoginSessionEntity> findByUserOrderByLoginTimeDesc(User user);
} 