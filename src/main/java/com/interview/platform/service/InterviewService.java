package com.interview.platform.service;

import com.interview.platform.model.*;
import com.interview.platform.repository.InterviewSessionRepository;
import com.interview.platform.repository.InterviewAnswerRepository;
import com.interview.platform.repository.AnswerAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InterviewService {

    @Autowired
    private InterviewSessionRepository sessionRepository;

    @Autowired
    private InterviewAnswerRepository answerRepository;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerAnalysisRepository analysisRepository;

    // ✅ Create a new interview session
    public InterviewSession createSession(User user, String sessionType, String category) {
        InterviewSession session = new InterviewSession();
        session.setUser(user);
        session.setSessionType(InterviewSession.SessionType.valueOf(sessionType));
        session.setStartTime(LocalDateTime.now());
        session.setStatus(InterviewSession.Status.InProgress);

        int totalQuestions = switch (sessionType) {
            case "Quick" -> 5;
            case "FullMock" -> 15;
            default -> 10;
        };
        session.setTotalQuestions(totalQuestions);

        return sessionRepository.save(session);
    }

    // ✅ Fetch session by ID
    public InterviewSession getSessionById(Long sessionId) {
        return sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found with ID: " + sessionId));
    }

    // ✅ Get all sessions for a user
    public List<InterviewSession> getUserSessions(User user) {
        return sessionRepository.findByUserOrderByStartTimeDesc(user);
    }

    // ✅ Save answer for a question
    public InterviewAnswer saveAnswer(Long sessionId, Long questionId, String videoPath,
                                      String audioPath, String transcription, Integer duration) {
        InterviewSession session = getSessionById(sessionId);
        Question question = questionService.getQuestionById(questionId);

        InterviewAnswer answer = new InterviewAnswer();
        answer.setSession(session);
        answer.setQuestion(question);
        answer.setUser(session.getUser());
        answer.setVideoPath(videoPath);
        answer.setAudioPath(audioPath);
        answer.setTranscription(transcription);
        answer.setDuration(duration);
        answer.setAnsweredAt(LocalDateTime.now());

        return answerRepository.save(answer);
    }

    // ✅ Update how many questions have been answered
    public void updateSessionProgress(Long sessionId) {
        InterviewSession session = getSessionById(sessionId);
        List<InterviewAnswer> answers = answerRepository.findBySession(session);
        session.setQuestionsAnswered(answers.size());
        sessionRepository.save(session);
    }

    // ✅ Mark session as completed
    public void completeSession(Long sessionId) {
        InterviewSession session = getSessionById(sessionId);
        session.setStatus(InterviewSession.Status.Completed);
        session.setEndTime(LocalDateTime.now());
        sessionRepository.save(session);
    }

    // ✅ Calculate user's average score across all sessions
    public Double getUserAverageScore(User user) {
        Double avg = sessionRepository.findAverageScoreByUser(user);
        return avg != null ? avg : 0.0;
    }

    // ✅ Count total interviews for a user
    public Integer getTotalInterviewsCount(User user) {
        Long count = sessionRepository.countByUser(user);
        return count != null ? count.intValue() : 0;
    }

    // ✅ Improved: Recalculate and update overall session score from all analyzed answers
    public void recalculateSessionScore(Long sessionId) {
        InterviewSession session = getSessionById(sessionId);
        List<InterviewAnswer> answers = answerRepository.findBySession(session);

        if (answers == null || answers.isEmpty()) {
            session.setOverallScore(0.0);
            sessionRepository.save(session);
            return;
        }

        double totalScore = 0.0;
        int count = 0;

        for (InterviewAnswer answer : answers) {
            // ✅ Case 1: Analysis already linked in the entity
            if (answer.getAnalysis() != null && answer.getAnalysis().getOverallScore() != null) {
                totalScore += answer.getAnalysis().getOverallScore();
                count++;
            }
            // ✅ Case 2: Try fetching from repository if not loaded
            else {
                Optional<AnswerAnalysis> analysisOpt = analysisRepository.findByAnswer(answer);
                if (analysisOpt.isPresent() && analysisOpt.get().getOverallScore() != null) {
                    totalScore += analysisOpt.get().getOverallScore();
                    count++;
                }
            }
        }

        // ✅ Calculate and save the average
        double avgScore = (count > 0) ? (totalScore / count) : 0.0;
        session.setOverallScore(avgScore);
        sessionRepository.save(session);
    }
}
