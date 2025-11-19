package com.interview.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "interview_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    private Long sessionId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "session_type")
    private SessionType sessionType = SessionType.Quick;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "total_questions")
    private Integer totalQuestions = 0;
    
    @Column(name = "questions_answered")
    private Integer questionsAnswered = 0;
    
    @Column(name = "overall_score")
    private Double overallScore = 0.0;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status = Status.InProgress;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL)
    private List<InterviewAnswer> answers;
    
    @PrePersist
    protected void onCreate() {
        startTime = LocalDateTime.now();
    }
    
    public enum SessionType {
        Quick, FullMock, Custom, ResumeBased
    }
    
    public enum Status {
        InProgress, Completed, Abandoned
    }
}