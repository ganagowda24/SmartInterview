package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SessionDTO {
    private Long sessionId;
    private String sessionType;
    private String startTime;
    private Integer totalQuestions;
    private Integer questionsAnswered;
    private Double overallScore;
    private String status;
}
