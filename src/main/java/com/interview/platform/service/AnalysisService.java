package com.interview.platform.service;

import com.interview.platform.dto.FeedbackDTO;
import com.interview.platform.model.AnswerAnalysis;
import com.interview.platform.model.InterviewAnswer;
import com.interview.platform.model.Question;
import com.interview.platform.repository.AnswerAnalysisRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class AnalysisService {
    
    @Autowired
    private AnswerAnalysisRepository analysisRepository;
    
    public FeedbackDTO analyzeAnswer(InterviewAnswer answer) {
        String transcription = answer.getTranscription();
        Integer duration = answer.getDuration();
        Question question = answer.getQuestion();
        
        System.out.println("=== ANALYZING ANSWER ===");
        System.out.println("Transcription: " + transcription);
        System.out.println("Duration: " + duration);
        System.out.println("Question ID: " + (question != null ? question.getQuestionId() : "NULL"));
        
        // Handle null/empty transcription
        if (transcription == null || transcription.trim().isEmpty()) {
            System.err.println("ERROR: Transcription is null or empty!");
            transcription = "No answer provided";
        }
        
        // Handle null duration
        if (duration == null || duration == 0) {
            duration = 60; // Default 1 minute
        }
        
        // Perform analysis
        int fillerCount = countFillerWords(transcription);
        int wpm = calculateWordsPerMinute(transcription, duration);
        double keywordScore = calculateKeywordMatch(transcription, question != null ? question.getExpectedKeywords() : "");
        
        System.out.println("Filler words: " + fillerCount);
        System.out.println("WPM: " + wpm);
        System.out.println("Keyword score: " + keywordScore);
        
        // Calculate scores
        double communicationScore = calculateCommunicationScore(fillerCount, wpm);
        double contentScore = keywordScore;
        double confidenceScore = 7.5; // Placeholder
        
        double overallScore = (contentScore * 0.4) + (communicationScore * 0.35) + (confidenceScore * 0.25);
        
        System.out.println("Content Score: " + contentScore);
        System.out.println("Communication Score: " + communicationScore);
        System.out.println("Overall Score: " + overallScore);
        
        // Generate feedback
        List<String> strengths = generateStrengths(communicationScore, contentScore);
        List<String> weaknesses = generateWeaknesses(fillerCount, wpm, keywordScore);
        List<String> tips = generateTips(fillerCount, keywordScore);
        
        // Save analysis
        AnswerAnalysis analysis = new AnswerAnalysis();
        analysis.setAnswer(answer);
        analysis.setContentScore(contentScore);
        analysis.setCommunicationScore(communicationScore);
        analysis.setConfidenceScore(confidenceScore);
        analysis.setOverallScore(overallScore);
        analysis.setWordsPerMinute(wpm);
        analysis.setFillerWordCount(fillerCount);
        analysis.setKeywordMatchPercentage(keywordScore * 10);
        analysis.setStrengths(String.join("|", strengths));
        analysis.setWeaknesses(String.join("|", weaknesses));
        analysis.setImprovementTips(String.join("|", tips));
        
        analysisRepository.save(analysis);
        
        // Update session score
        updateSessionScore(answer.getSession().getSessionId());
        
        // Create DTO
        FeedbackDTO feedback = new FeedbackDTO();
        feedback.setAnswerId(answer.getAnswerId());
        feedback.setOverallScore(overallScore);
        feedback.setContentScore(contentScore);
        feedback.setCommunicationScore(communicationScore);
        feedback.setConfidenceScore(confidenceScore);
        feedback.setWordsPerMinute(wpm);
        feedback.setFillerWordCount(fillerCount);
        feedback.setKeywordMatchPercentage(keywordScore * 10);
        feedback.setStrengths(strengths);
        feedback.setWeaknesses(weaknesses);
        feedback.setTips(tips);
        feedback.setTranscription(transcription);
        
        System.out.println("=== ANALYSIS COMPLETE ===");
        
        return feedback;
    }
    
    private void updateSessionScore(Long sessionId) {
        // This should be called to update the session's overall score
        // Add logic to calculate average score of all answers in the session
    }
    
    private int countFillerWords(String text) {
        if (text == null || text.isEmpty()) return 0;
        
        String[] fillers = {"\\bum\\b", "\\buh\\b", "\\blike\\b", "\\byou know\\b", 
                           "\\bbasically\\b", "\\bactually\\b", "\\bsort of\\b", 
                           "\\bkind of\\b", "\\bliterally\\b"};
        int count = 0;
        String lowerText = text.toLowerCase();
        
        for (String filler : fillers) {
            Pattern pattern = Pattern.compile(filler);
            Matcher matcher = pattern.matcher(lowerText);
            while (matcher.find()) {
                count++;
            }
        }
        return count;
    }
    
    private int calculateWordsPerMinute(String text, Integer durationSeconds) {
        if (text == null || text.isEmpty() || durationSeconds == null || durationSeconds == 0) 
            return 0;
        
        int wordCount = text.trim().split("\\s+").length;
        double minutes = durationSeconds / 60.0;
        return (int) (wordCount / minutes);
    }
    
    private double calculateKeywordMatch(String answer, String keywords) {
        if (answer == null || keywords == null || keywords.isEmpty()) return 5.0;
        
        String[] keywordArray = keywords.split(",");
        int matches = 0;
        String lowerAnswer = answer.toLowerCase();
        
        for (String keyword : keywordArray) {
            if (lowerAnswer.contains(keyword.toLowerCase().trim())) {
                matches++;
            }
        }
        
        double percentage = ((double) matches / keywordArray.length);
        return percentage * 10.0; // Convert to 0-10 scale
    }
    
    private double calculateCommunicationScore(int fillerCount, int wpm) {
        double score = 10.0;
        
        // Penalize for filler words
        score -= Math.min(fillerCount * 0.2, 4.0);
        
        // Penalize for too fast or too slow speech
        if (wpm > 0) {
            if (wpm < 100 || wpm > 180) {
                score -= 2.0;
            }
        }
        
        return Math.max(score, 0.0);
    }
    
    private List<String> generateStrengths(double communicationScore, double contentScore) {
        List<String> strengths = new ArrayList<>();
        
        if (communicationScore >= 7.0) {
            strengths.add("Clear and confident communication");
        }
        if (contentScore >= 7.0) {
            strengths.add("Good coverage of key concepts");
        }
        if (strengths.isEmpty()) {
            strengths.add("Completed the answer");
        }
        
        return strengths;
    }
    
    private List<String> generateWeaknesses(int fillerCount, int wpm, double keywordScore) {
        List<String> weaknesses = new ArrayList<>();
        
        if (fillerCount > 5) {
            weaknesses.add("Too many filler words detected (" + fillerCount + ")");
        }
        if (wpm > 180) {
            weaknesses.add("Speaking too fast (" + wpm + " words/min)");
        } else if (wpm < 100 && wpm > 0) {
            weaknesses.add("Speaking too slowly (" + wpm + " words/min)");
        }
        if (keywordScore < 50) {
            weaknesses.add("Missing important keywords in answer");
        }
        if (weaknesses.isEmpty()) {
            weaknesses.add("Keep practicing for improvement");
        }
        
        return weaknesses;
    }
    
    private List<String> generateTips(int fillerCount, double keywordScore) {
        List<String> tips = new ArrayList<>();
        
        if (fillerCount > 5) {
            tips.add("Practice answering without filler words by pausing instead");
        }
        if (keywordScore < 50) {
            tips.add("Review the expected answer and include key concepts");
        }
        tips.add("Practice this question multiple times to improve");
        
        return tips;
    }
}