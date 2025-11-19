package com.interview.platform.controller;

import com.interview.platform.dto.*;
import com.interview.platform.model.User;
import com.interview.platform.security.JwtUtil;
import com.interview.platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody UserRegistrationDTO dto) {
        if(userService.existsByEmail(dto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, "Email already exists"));
        }
        try {
            User user = userService.registerUser(dto);
            return ResponseEntity.ok(new ApiResponse(true, "Registration successful", user.getEmail()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Registration failed: " + e.getMessage()));
        }
    }

    
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginDTO dto) {
        try {
            boolean authenticated = userService.authenticateUser(dto.getEmail(), dto.getPassword());
            if (!authenticated) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Invalid email or password"));
            }

            User user = userService.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtUtil.generateToken(user.getEmail());
            userService.updateLastLogin(dto.getEmail());

            LoginResponseDTO response = new LoginResponseDTO(
                    token, user.getEmail(), user.getFullName(), user.getRole(), "Login successful"
            );

            return ResponseEntity.ok(new ApiResponse(true, "Login successful", response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(false, "Login failed: " + e.getMessage()));
        }
    }

}
