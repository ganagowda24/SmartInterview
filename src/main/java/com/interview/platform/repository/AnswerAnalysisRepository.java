package com.interview.platform.repository;

import com.interview.platform.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AnswerAnalysisRepository extends JpaRepository<AnswerAnalysis, Long> {
    Optional<AnswerAnalysis> findByAnswer(InterviewAnswer answer);
    
    @Query("SELECT AVG(a.overallScore) FROM AnswerAnalysis a JOIN a.answer ans WHERE ans.user = :user")
    Double findAverageScoreByUser(@Param("user") User user);
}
