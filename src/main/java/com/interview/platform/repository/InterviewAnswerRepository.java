package com.interview.platform.repository;

import com.interview.platform.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface InterviewAnswerRepository extends JpaRepository<InterviewAnswer, Long> {
    List<InterviewAnswer> findBySession(InterviewSession session);
    List<InterviewAnswer> findByUser(User user);
    List<InterviewAnswer> findByQuestion(Question question);
    
    @Query("SELECT a FROM InterviewAnswer a WHERE a.user = :user AND a.question = :question ORDER BY a.answeredAt DESC")
    List<InterviewAnswer> findByUserAndQuestionOrderByAnsweredAtDesc(@Param("user") User user, 
                                                                       @Param("question") Question question);
}
