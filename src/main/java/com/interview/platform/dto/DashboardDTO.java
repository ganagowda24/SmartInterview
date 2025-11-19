package com.interview.platform.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DashboardDTO {
    private String userName;
    private Integer totalInterviews;
    private Double averageScore;
    private Integer totalQuestionsAnswered;
    private List<SessionDTO> recentSessions;
    private List<String> topCategories;
}