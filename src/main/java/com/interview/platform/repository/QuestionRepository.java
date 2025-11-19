package com.interview.platform.repository;

import com.interview.platform.model.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByCategory(String category);
    List<Question> findByDifficulty(Question.Difficulty difficulty);
    List<Question> findByCategoryAndDifficulty(String category, Question.Difficulty difficulty);
    List<Question> findByIsActiveTrue();

    // ✅ Random questions by category
    @Query(value = "SELECT * FROM questions WHERE category = :category AND is_active = true ORDER BY RAND()", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(@Param("category") String category, Pageable pageable);

    // ✅ Random questions from all categories
    @Query(value = "SELECT * FROM questions WHERE is_active = true ORDER BY RAND()", nativeQuery = true)
    List<Question> findRandomQuestions(Pageable pageable);
}
