package com.interview.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;
    
    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    
    @Column(name = "phone", length = 15)
    private String phone;
    
    @Column(name = "industry", length = 50)
    private String industry;
    
    @Column(name = "target_role", length = 100)
    private String targetRole;
    
    @Column(name = "experience_level", length = 20)
    private String experienceLevel;
    
    @Column(name = "resume_path", length = 255)
    private String resumePath;
    
    @Column(name = "profile_picture", length = 255)
    private String profilePicture;
    
    @Column(name = "trust_score")
    private Double trustScore = 5.0;
    
    @Column(name = "role", length = 20)
    private String role = "USER";
    
    @Column(name = "enabled")
    private Boolean enabled = true;
    
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<InterviewSession> interviewSessions;
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserSkill> skills;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}