package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackDTO {
    private Long answerId;
    private Double overallScore;
    private Double contentScore;
    private Double communicationScore;
    private Double confidenceScore;
    private Integer wordsPerMinute;
    private Integer fillerWordCount;
    private Double keywordMatchPercentage;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> tips;
    private String transcription;
}