package com.interview.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "answer_analysis")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnswerAnalysis {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "analysis_id")
    private Long analysisId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "answer_id", nullable = false)
    private InterviewAnswer answer;
    
    @Column(name = "content_score")
    private Double contentScore = 0.0;
    
    @Column(name = "communication_score")
    private Double communicationScore = 0.0;
    
    @Column(name = "confidence_score")
    private Double confidenceScore = 0.0;
    
    @Column(name = "overall_score")
    private Double overallScore = 0.0;
    
    @Column(name = "words_per_minute")
    private Integer wordsPerMinute = 0;
    
    @Column(name = "filler_word_count")
    private Integer fillerWordCount = 0;
    
    @Column(name = "pause_count")
    private Integer pauseCount = 0;
    
    @Column(name = "keyword_match_percentage")
    private Double keywordMatchPercentage = 0.0;
    
    @Column(name = "answer_completeness")
    private Double answerCompleteness = 0.0;
    
    @Column(name = "strengths", columnDefinition = "TEXT")
    private String strengths;
    
    @Column(name = "weaknesses", columnDefinition = "TEXT")
    private String weaknesses;
    
    @Column(name = "improvement_tips", columnDefinition = "TEXT")
    private String improvementTips;
    
    @Column(name = "analyzed_at")
    private LocalDateTime analyzedAt;
    
    @PrePersist
    protected void onCreate() {
        analyzedAt = LocalDateTime.now();
    }
}