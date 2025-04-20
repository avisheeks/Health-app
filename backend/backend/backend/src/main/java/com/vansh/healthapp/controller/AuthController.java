package com.vansh.healthapp.controller;

import com.vansh.healthapp.model.Role;
import com.vansh.healthapp.model.User;
import com.vansh.healthapp.payload.request.LoginRequest;
import com.vansh.healthapp.payload.request.SignupRequest;
import com.vansh.healthapp.payload.response.JwtAuthResponse;
import com.vansh.healthapp.payload.response.MessageResponse;
import com.vansh.healthapp.repository.RoleRepository;
import com.vansh.healthapp.repository.UserRepository;
import com.vansh.healthapp.security.JwtTokenProvider;
import com.vansh.healthapp.service.DoctorService;
import com.vansh.healthapp.service.PatientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final PatientService patientService;
    private final DoctorService doctorService;

    public AuthController(
            AuthenticationManager authenticationManager,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider tokenProvider,
            PatientService patientService,
            DoctorService doctorService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.patientService = patientService;
        this.doctorService = doctorService;
    }



    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(

                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            // Get user details
            User user = userRepository.findByEmail(loginRequest.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Verify role if provided
            if (loginRequest.getRole() != null && !loginRequest.getRole().isEmpty()) {
                boolean hasRole = user.getRoles().stream()
                        .anyMatch(role -> role.getName().equals(loginRequest.getRole().toUpperCase()));

                if (!hasRole) {
                    return ResponseEntity
                            .status(HttpStatus.UNAUTHORIZED)
                            .body(new MessageResponse("User does not have the required role"));
                }
            }

            // Create response with token and user details
            JwtAuthResponse response = new JwtAuthResponse(jwt, user);

            return ResponseEntity.ok(response);
        } catch (AuthenticationException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Invalid email or password"));
        }
    }


    // Add this new endpoint
    @GetMapping("/user")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String token) {
        // Remove "Bearer " from token
        String jwt = token.substring(7);
        String userEmail = tokenProvider.getUsernameFromJWT(jwt);
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Create a response object with necessary user details
        Map<String, Object> response = new HashMap<>();
        response.put("id", user.getId());
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("roles", user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList()));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                return ResponseEntity
                        .badRequest()
                        .body(new MessageResponse("Error: Email is already in use!"));
            }

            // Create new user's account
            User user = new User();
            user.setFirstName(signUpRequest.getFirstName());
            user.setLastName(signUpRequest.getLastName());
            user.setEmail(signUpRequest.getEmail());
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
            user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

            Set<Role> roles = new HashSet<>();
            String roleName; // Default role
            
            if (signUpRequest.getRole() != null && !signUpRequest.getRole().isEmpty()) {
                roleName = signUpRequest.getRole().toUpperCase();
            } else {
                roleName = "PATIENT";
            }

            Role userRole = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new RuntimeException("Error: Role '" + roleName +  "' is not found."));
            roles.add(userRole);

            user.setRoles(roles);
            User savedUser = userRepository.save(user);
            
            // Create either a Patient or Doctor based on the role
            if (roleName.equals("PATIENT")) {
                patientService.createPatient(savedUser);
            } else if (roleName.equals("DOCTOR")) {
                doctorService.createDoctor(savedUser);
            }

            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
        } catch (Exception e) {
            // Log the exception for troubleshooting
            System.err.println("Error during user registration: " + e.getMessage());
            e.printStackTrace();
            
            // Return a user-friendly error response
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Registration failed due to a server error. Please try again later."));
        }
    }

    // Add a simple test endpoint
    @GetMapping("/test")
    public ResponseEntity<String> testEndpoint() {
        return ResponseEntity.ok("Authentication service is working!");
    }

    // Add a validate-token endpoint
    @GetMapping("/validate-token")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            // Extract the token from the Authorization header
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            // Validate the token
            if (!tokenProvider.validateToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse("Invalid token"));
            }
            
            // Get user details from the token
            String userEmail = tokenProvider.getUsernameFromJWT(token);
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
                
            // Create response with user details
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("firstName", user.getFirstName());
            response.put("lastName", user.getLastName());
            response.put("roles", user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList()));
                
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Token validation failed: " + e.getMessage()));
        }
    }
} 