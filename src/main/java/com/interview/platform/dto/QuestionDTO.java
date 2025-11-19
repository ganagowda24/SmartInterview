package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long questionId;
    private String questionText;
    private String category;
    private String subcategory;
    private String difficulty;
    private String tips;
    private Integer idealDuration;
}