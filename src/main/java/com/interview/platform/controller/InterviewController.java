package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.FeedbackDTO;
import com.interview.platform.dto.SessionDTO;
import com.interview.platform.model.InterviewAnswer;
import com.interview.platform.model.InterviewSession;
import com.interview.platform.model.User;
import com.interview.platform.service.AnalysisService;
import com.interview.platform.service.InterviewService;
import com.interview.platform.service.SpeechToTextService;
import com.interview.platform.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/interview")
@CrossOrigin(origins = "*")
public class InterviewController {

    @Autowired
    private InterviewService interviewService;

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private UserService userService;

    @Autowired
    private SpeechToTextService speechToTextService;

    // ====================== START INTERVIEW ======================
    @PostMapping("/start")
    public ResponseEntity<ApiResponse> startInterview(
            @RequestParam String sessionType,
            @RequestParam(required = false) String category,
            Authentication authentication) {
        try {
            System.out.println("=== START INTERVIEW REQUEST ===");
            System.out.println("Session Type: " + sessionType);
            System.out.println("Category: " + category);

            if (authentication == null || authentication.getName() == null) {
                System.err.println("ERROR: Authentication is null!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse(false, "Authentication required. Please login again."));
            }

            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found: " + authentication.getName()));

            System.out.println("User found: " + user.getEmail());

            InterviewSession session = interviewService.createSession(user, sessionType, category);
            SessionDTO dto = convertToSessionDTO(session);

            System.out.println("Session created successfully: " + session.getSessionId());
            System.out.println("=== END START INTERVIEW REQUEST ===");

            return ResponseEntity.ok(new ApiResponse(true, "Interview session started", dto));
        } catch (Exception e) {
            System.err.println("ERROR starting interview: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to start session: " + e.getMessage()));
        }
    }

    // ====================== SUBMIT TEXT ANSWER ======================
    @PostMapping("/submit-answer")
    public ResponseEntity<ApiResponse> submitAnswer(
            @RequestParam Long sessionId,
            @RequestParam Long questionId,
            @RequestParam String transcription,
            @RequestParam Integer duration,
            Authentication authentication) {
        try {
            System.out.println("=== SUBMIT TEXT ANSWER ===");
            String videoPath = "videos/session_" + sessionId + "_q" + questionId + ".mp4";
            String audioPath = "audio/session_" + sessionId + "_q" + questionId + ".wav";

            InterviewAnswer answer = interviewService.saveAnswer(
                    sessionId, questionId, videoPath, audioPath, transcription, duration);

            // 🧠 Analyze answer
            FeedbackDTO feedback = analysisService.analyzeAnswer(answer);

            // 🔁 Handle post-analysis processing
            postAnalysisProcessing(sessionId);

            System.out.println("=== END SUBMIT TEXT ANSWER ===");
            return ResponseEntity.ok(new ApiResponse(true, "Answer submitted and analyzed", feedback));

        } catch (Exception e) {
            System.err.println("ERROR submitting answer: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to submit answer: " + e.getMessage()));
        }
    }

    // ====================== SUBMIT AUDIO ANSWER ======================
    @PostMapping("/submit-answer-with-audio")
    public ResponseEntity<ApiResponse> submitAnswerWithAudio(
            @RequestParam Long sessionId,
            @RequestParam Long questionId,
            @RequestParam(required = false) MultipartFile audioFile,
            @RequestParam(required = false) String transcription,
            Authentication authentication) {
        try {
            System.out.println("=== SUBMIT AUDIO ANSWER ===");
            String finalTranscription;

            // 🎙 If audio provided → transcribe
            if (audioFile != null && !audioFile.isEmpty()) {
                String audioPath = speechToTextService.saveAudioFile(audioFile, sessionId, questionId);
                finalTranscription = speechToTextService.transcribeAudio(audioFile);
                System.out.println("✅ Audio transcribed: " + finalTranscription);
            }
            // ✍️ If manual text provided
            else if (transcription != null && !transcription.isEmpty()) {
                finalTranscription = transcription;
            }
            // ❌ No input provided
            else {
                return ResponseEntity.badRequest()
                        .body(new ApiResponse(false, "Either audio file or transcription required"));
            }

            // ⏱ Estimate duration
            int duration = estimateDuration(finalTranscription);

            // 💾 Save answer
            InterviewAnswer answer = interviewService.saveAnswer(
                    sessionId, questionId, null, null, finalTranscription, duration);

            // 🧠 Analyze
            FeedbackDTO feedback = analysisService.analyzeAnswer(answer);

            // 🔁 Handle post-analysis processing
            postAnalysisProcessing(sessionId);

            System.out.println("=== END SUBMIT AUDIO ANSWER ===");
            return ResponseEntity.ok(new ApiResponse(true, "Answer submitted and analyzed", feedback));
        } catch (Exception e) {
            System.err.println("ERROR submitting audio answer: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to submit audio answer: " + e.getMessage()));
        }
    }

    // ====================== COMPLETE SESSION ======================
    @PostMapping("/complete/{sessionId}")
    public ResponseEntity<ApiResponse> completeInterview(@PathVariable Long sessionId) {
        try {
            System.out.println("=== COMPLETE INTERVIEW SESSION ===");
            interviewService.completeSession(sessionId);
            InterviewSession session = interviewService.getSessionById(sessionId);
            SessionDTO dto = convertToSessionDTO(session);
            System.out.println("Interview session " + sessionId + " marked as complete.");
            return ResponseEntity.ok(new ApiResponse(true, "Interview completed", dto));
        } catch (Exception e) {
            System.err.println("ERROR completing session: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to complete session: " + e.getMessage()));
        }
    }

    // ====================== GET USER SESSIONS ======================
    @GetMapping("/sessions")
    public ResponseEntity<ApiResponse> getUserSessions(Authentication authentication) {
        try {
            User user = userService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<InterviewSession> sessions = interviewService.getUserSessions(user);
            List<SessionDTO> dtos = sessions.stream()
                    .map(this::convertToSessionDTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new ApiResponse(true, "Sessions retrieved", dtos));
        } catch (Exception e) {
            System.err.println("ERROR fetching sessions: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve sessions: " + e.getMessage()));
        }
    }

    // ====================== HELPER METHODS ======================

    // Estimate answer duration
    private int estimateDuration(String text) {
        int wordCount = text.split("\\s+").length;
        return (int) ((wordCount / 150.0) * 60); // Approx 150 WPM
    }

    // Post-analysis handler: updates progress + recalculates session score
    private void postAnalysisProcessing(Long sessionId) {
        try {
            System.out.println("=== POST ANALYSIS PROCESSING ===");
            interviewService.updateSessionProgress(sessionId);
            System.out.println("Progress updated. Recalculating session score...");
            interviewService.recalculateSessionScore(sessionId);
            System.out.println("✅ Session score recalculated successfully!");
        } catch (Exception e) {
            System.err.println("ERROR during post-analysis processing: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Convert Entity → DTO
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
