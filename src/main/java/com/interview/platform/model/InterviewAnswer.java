package com.interview.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "interview_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class InterviewAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_id")
    private Long answerId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private InterviewSession session;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "video_path", length = 255)
    private String videoPath;
    
    @Column(name = "audio_path", length = 255)
    private String audioPath;
    
    @Column(name = "transcription", columnDefinition = "TEXT")
    private String transcription;
    
    @Column(name = "duration")
    private Integer duration = 0;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    @OneToOne(mappedBy = "answer", cascade = CascadeType.ALL)
    private AnswerAnalysis analysis;
    
    @PrePersist
    protected void onCreate() {
        answeredAt = LocalDateTime.now();
    }
}