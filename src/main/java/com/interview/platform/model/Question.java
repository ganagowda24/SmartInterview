package com.interview.platform.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "questions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Long questionId;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "subcategory", length = 50)
    private String subcategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty")
    private Difficulty difficulty = Difficulty.Medium;

    @Column(name = "expected_keywords", columnDefinition = "TEXT")
    private String expectedKeywords;

    @Column(name = "ideal_duration")
    private Integer idealDuration = 180;

    @Column(name = "tips", columnDefinition = "TEXT")
    private String tips;

    @Column(name = "sample_answer", columnDefinition = "TEXT")
    private String sampleAnswer;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public enum Difficulty {
        Easy, Medium, Hard
    }

    // ✅ Add this custom setter to handle String inputs
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public void setDifficulty(String difficulty) {
        if (difficulty != null) {
            try {
                this.difficulty = Difficulty.valueOf(capitalizeFirst(difficulty));
            } catch (IllegalArgumentException e) {
                this.difficulty = Difficulty.Medium; // default or fallback
            }
        } else {
            this.difficulty = Difficulty.Medium;
        }
    }

    private String capitalizeFirst(String value) {
        if (value == null || value.isEmpty()) return value;
        return value.substring(0, 1).toUpperCase() + value.substring(1).toLowerCase();
    }

}
