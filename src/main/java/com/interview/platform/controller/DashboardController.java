package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.DashboardDTO;
import com.interview.platform.dto.SessionDTO;
import com.interview.platform.model.InterviewSession;
import com.interview.platform.model.User;
import com.interview.platform.service.InterviewService;
import com.interview.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private InterviewService interviewService;
    
    @GetMapping
    public ResponseEntity<ApiResponse> getDashboard(Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            
            DashboardDTO dashboard = new DashboardDTO();
            dashboard.setUserName(user.getFullName());
            dashboard.setTotalInterviews(interviewService.getTotalInterviewsCount(user));
            dashboard.setAverageScore(interviewService.getUserAverageScore(user));
            
            List<InterviewSession> sessions = interviewService.getUserSessions(user);
            List<SessionDTO> recentSessions = sessions.stream()
                    .limit(5)
                    .map(this::convertToSessionDTO)
                    .collect(Collectors.toList());
            dashboard.setRecentSessions(recentSessions);
            
            int totalQuestions = sessions.stream()
                    .mapToInt(InterviewSession::getQuestionsAnswered)
                    .sum();
            dashboard.setTotalQuestionsAnswered(totalQuestions);
            dashboard.setTopCategories(List.of("Technical", "Behavioral", "HR"));
            
            return ResponseEntity.ok(new ApiResponse(true, "Dashboard data retrieved", dashboard));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to load dashboard: " + e.getMessage()));
        }
    }
    
    private SessionDTO convertToSessionDTO(InterviewSession session) {
        SessionDTO dto = new SessionDTO();
        dto.setSessionId(session.getSessionId());
        dto.setSessionType(session.getSessionType().toString());
        dto.setStartTime(session.getStartTime().toString());
        dto.setTotalQuestions(session.getTotalQuestions());
        dto.setQuestionsAnswered(session.getQuestionsAnswered());
        dto.setOverallScore(session.getOverallScore());
        dto.setStatus(session.getStatus().toString());
        return dto;
    }
}