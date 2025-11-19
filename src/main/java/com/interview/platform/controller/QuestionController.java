package com.interview.platform.controller;

import com.interview.platform.dto.ApiResponse;
import com.interview.platform.dto.QuestionDTO;
import com.interview.platform.model.Question;
import com.interview.platform.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // ✅ GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        List<QuestionDTO> dtos = questions.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse(true, "Questions retrieved", dtos));
    }

    // ✅ GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getQuestionById(@PathVariable Long id) {
        try {
            Question question = questionService.getQuestionById(id);
            return ResponseEntity.ok(new ApiResponse(true, "Question found", convertToDTO(question)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Question not found", null));
        }
    }

    // ✅ GET RANDOM
    @GetMapping("/random")
    public ResponseEntity<ApiResponse> getRandomQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "5") int count) {
        List<Question> questions = questionService.getRandomQuestions(category, count);
        List<QuestionDTO> dtos = questions.stream().map(this::convertToDTO).collect(Collectors.toList());
        return ResponseEntity.ok(new ApiResponse(true, "Random questions retrieved", dtos));
    }

    // ✅ CREATE (POST)
    @PostMapping
    public ResponseEntity<ApiResponse> addQuestion(@RequestBody QuestionDTO dto) {
        Question question = convertToEntity(dto);
        questionService.saveQuestion(question);
        return ResponseEntity.ok(new ApiResponse(true, "Question added successfully", convertToDTO(question)));
    }

    // ✅ UPDATE (PUT)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateQuestion(@PathVariable Long id, @RequestBody QuestionDTO dto) {
        try {
            Question existing = questionService.getQuestionById(id);
            existing.setQuestionText(dto.getQuestionText());
            existing.setCategory(dto.getCategory());
            existing.setSubcategory(dto.getSubcategory());
            existing.setDifficulty(dto.getDifficulty());
            existing.setTips(dto.getTips());
            existing.setIdealDuration(dto.getIdealDuration());

            questionService.saveQuestion(existing);
            return ResponseEntity.ok(new ApiResponse(true, "Question updated successfully", convertToDTO(existing)));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Question not found", null));
        }
    }

    // ✅ DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteQuestion(@PathVariable Long id) {
        try {
            questionService.deleteQuestion(id);
            return ResponseEntity.ok(new ApiResponse(true, "Question deleted successfully", null));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, "Question not found", null));
        }
    }

    // 🔁 Convert Entity → DTO
    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = new QuestionDTO();
        dto.setQuestionId(question.getQuestionId());
        dto.setQuestionText(question.getQuestionText());
        dto.setCategory(question.getCategory());
        dto.setSubcategory(question.getSubcategory());
        dto.setDifficulty(question.getDifficulty() != null ? question.getDifficulty().toString() : null);
        dto.setTips(question.getTips());
        dto.setIdealDuration(question.getIdealDuration());
        return dto;
    }

    // 🔁 Convert DTO → Entity
    private Question convertToEntity(QuestionDTO dto) {
        Question question = new Question();
        question.setQuestionId(dto.getQuestionId());
        question.setQuestionText(dto.getQuestionText());
        question.setCategory(dto.getCategory());
        question.setSubcategory(dto.getSubcategory());
        question.setDifficulty(dto.getDifficulty());
        question.setTips(dto.getTips());
        question.setIdealDuration(dto.getIdealDuration());
        return question;
    }
}
