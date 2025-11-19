package com.interview.platform.service;

import com.interview.platform.model.Question;
import com.interview.platform.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    // ✅ Get all active questions
    public List<Question> getAllQuestions() {
        return questionRepository.findByIsActiveTrue();
    }

    // ✅ Get question by ID
    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
    }

    // ✅ Get questions by category
    public List<Question> getQuestionsByCategory(String category) {
        return questionRepository.findByCategory(category);
    }

    // ✅ Get random questions (category optional)
    public List<Question> getRandomQuestions(String category, int count) {
        if (category != null && !category.isEmpty()) {
            return questionRepository.findRandomQuestionsByCategory(category, PageRequest.of(0, count));
        }
        return questionRepository.findRandomQuestions(PageRequest.of(0, count));
    }

    // ✅ Add or update a question (used by controller)
    public Question saveQuestion(Question question) {
        question.setIsActive(true);
        return questionRepository.save(question);
    }

    // ✅ Delete question by ID (soft delete optional)
    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        questionRepository.delete(question);
    }

    // ✅ Create new question (alias for saveQuestion)
    public Question createQuestion(Question question) {
        question.setIsActive(true);
        return questionRepository.save(question);
    }
}
