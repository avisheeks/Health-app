package com.vansh.healthapp.service;

import com.vansh.healthapp.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    
    User getUserById(Long id);
    
    User updateUser(Long id, User userDetails);
    
    void deleteUser(Long id);

    User getUserByEmail(String email);

    User getCurrentUser();


} 