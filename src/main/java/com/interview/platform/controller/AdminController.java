package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.model.Question;
import com.interview.platform.model.User;
import com.interview.platform.repository.UserRepository;
import com.interview.platform.repository.QuestionRepository;
import com.interview.platform.repository.InterviewSessionRepository;
import com.interview.platform.service.QuestionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private InterviewSessionRepository sessionRepository;

    @Autowired
    private QuestionService questionService;

    // ==================== DASHBOARD STATS ====================
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getAdminStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalUsers", userRepository.count());
            stats.put("totalQuestions", questionRepository.count());
            stats.put("totalSessions", sessionRepository.count());
            stats.put("activeQuestions", questionRepository.findByIsActiveTrue().size());

            // Calculate average score
            double avgScore = sessionRepository.findAll().stream()
                    .filter(s -> s.getOverallScore() != null)
                    .mapToDouble(s -> s.getOverallScore())
                    .average()
                    .orElse(0.0);

            stats.put("averageScore", avgScore);

            return ResponseEntity.ok(new ApiResponse(true, "Dashboard stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching stats: " + e.getMessage()));
        }
    }

    // ==================== USER MANAGEMENT ====================
    @GetMapping("/users")
    public ResponseEntity<ApiResponse> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return ResponseEntity.ok(new ApiResponse(true, "Users retrieved", users));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching users: " + e.getMessage()));
        }
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<ApiResponse> getUserById(@PathVariable Long id) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(new ApiResponse(true, "User found", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @GetMapping("/users/count")
    public ResponseEntity<ApiResponse> getUserCount() {
        return ResponseEntity.ok(new ApiResponse(true, "User count", userRepository.count()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        try {
            userRepository.deleteById(id);
            return ResponseEntity.ok(new ApiResponse(true, "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error deleting user: " + e.getMessage()));
        }
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<ApiResponse> updateUserRole(@PathVariable Long id, @RequestParam String role) {
        try {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            user.setRole(role);
            userRepository.save(user);
            return ResponseEntity.ok(new ApiResponse(true, "User role updated", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error updating role: " + e.getMessage()));
        }
    }

    // ==================== QUESTION MANAGEMENT ====================
    @GetMapping("/questions/count")
    public ResponseEntity<ApiResponse> getQuestionCount() {
        return ResponseEntity.ok(new ApiResponse(true, "Question count", questionRepository.count()));
    }

    @GetMapping("/questions/by-category")
    public ResponseEntity<ApiResponse> getQuestionsByCategory() {
        try {
            List<Question> allQuestions = questionRepository.findAll();
            Map<String, Long> categoryCount = allQuestions.stream()
                    .collect(Collectors.groupingBy(Question::getCategory, Collectors.counting()));

            return ResponseEntity.ok(new ApiResponse(true, "Category stats", categoryCount));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching categories: " + e.getMessage()));
        }
    }

    @PutMapping("/questions/{id}")
    public ResponseEntity<ApiResponse> updateQuestion(@PathVariable Long id, @RequestBody Question updatedQuestion) {
        try {
            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Question not found"));

            question.setQuestionText(updatedQuestion.getQuestionText());
            question.setCategory(updatedQuestion.getCategory());
            question.setSubcategory(updatedQuestion.getSubcategory());
            question.setDifficulty(updatedQuestion.getDifficulty());
            question.setExpectedKeywords(updatedQuestion.getExpectedKeywords());
            question.setTips(updatedQuestion.getTips());

            questionRepository.save(question);
            return ResponseEntity.ok(new ApiResponse(true, "Question updated successfully", question));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error updating question: " + e.getMessage()));
        }
    }

    @DeleteMapping("/questions/{id}")
    public ResponseEntity<ApiResponse> deactivateQuestion(@PathVariable Long id) {
        try {
            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            question.setIsActive(false);
            questionRepository.save(question);
            return ResponseEntity.ok(new ApiResponse(true, "Question deactivated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error deactivating question: " + e.getMessage()));
        }
    }

    @PutMapping("/questions/{id}/activate")
    public ResponseEntity<ApiResponse> activateQuestion(@PathVariable Long id) {
        try {
            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            question.setIsActive(true);
            questionRepository.save(question);
            return ResponseEntity.ok(new ApiResponse(true, "Question activated"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error activating question: " + e.getMessage()));
        }
    }

    // ==================== SESSION MANAGEMENT ====================
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse> getAllSessions() {
        try {
            return ResponseEntity.ok(new ApiResponse(true, "Sessions retrieved", sessionRepository.findAll()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching sessions: " + e.getMessage()));
        }
    }

    @GetMapping("/sessions/count")
    public ResponseEntity<ApiResponse> getSessionCount() {
        return ResponseEntity.ok(new ApiResponse(true, "Session count", sessionRepository.count()));
    }

    @GetMapping("/sessions/stats")
    public ResponseEntity<ApiResponse> getSessionStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            long total = sessionRepository.count();
            long completed = sessionRepository.findAll().stream()
                    .filter(s -> "Completed".equalsIgnoreCase(s.getStatus().toString()))
                    .count();
            long inProgress = sessionRepository.findAll().stream()
                    .filter(s -> "InProgress".equalsIgnoreCase(s.getStatus().toString()))
                    .count();

            stats.put("total", total);
            stats.put("completed", completed);
            stats.put("inProgress", inProgress);
            stats.put("abandoned", total - completed - inProgress);

            return ResponseEntity.ok(new ApiResponse(true, "Session stats retrieved", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching session stats: " + e.getMessage()));
        }
    }

    // ==================== ANALYTICS ====================
    @GetMapping("/analytics/users-by-industry")
    public ResponseEntity<ApiResponse> getUsersByIndustry() {
        try {
            Map<String, Long> industryStats = userRepository.findAll().stream()
                    .collect(Collectors.groupingBy(
                            u -> Optional.ofNullable(u.getIndustry()).orElse("Not Specified"),
                            Collectors.counting()
                    ));
            return ResponseEntity.ok(new ApiResponse(true, "Industry analytics retrieved", industryStats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching industry analytics: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics/users-by-experience")
    public ResponseEntity<ApiResponse> getUsersByExperience() {
        try {
            Map<String, Long> expStats = userRepository.findAll().stream()
                    .collect(Collectors.groupingBy(
                            u -> Optional.ofNullable(u.getExperienceLevel()).orElse("Not Specified"),
                            Collectors.counting()
                    ));
            return ResponseEntity.ok(new ApiResponse(true, "Experience analytics retrieved", expStats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching experience analytics: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics/platform-usage")
    public ResponseEntity<ApiResponse> getPlatformUsage() {
        try {
            Map<String, Object> usage = new HashMap<>();
            usage.put("totalUsers", userRepository.count());
            usage.put("totalSessions", sessionRepository.count());
            usage.put("totalQuestions", questionRepository.count());
            usage.put("activeQuestions", questionRepository.findByIsActiveTrue().size());
            usage.put("monthlyActiveUsers", userRepository.count());

            return ResponseEntity.ok(new ApiResponse(true, "Platform usage retrieved", usage));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Error fetching platform usage: " + e.getMessage()));
        }
    }
}
