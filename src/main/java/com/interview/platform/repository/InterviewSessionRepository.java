package com.interview.platform.repository;

import com.interview.platform.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface InterviewSessionRepository extends JpaRepository<InterviewSession, Long> {
    List<InterviewSession> findByUser(User user);
    List<InterviewSession> findByUserOrderByStartTimeDesc(User user);
    
    @Query("SELECT s FROM InterviewSession s WHERE s.user = :user AND s.status = :status")
    List<InterviewSession> findByUserAndStatus(@Param("user") User user, 
                                                @Param("status") InterviewSession.Status status);
    
    @Query("SELECT AVG(s.overallScore) FROM InterviewSession s WHERE s.user = :user AND s.status = 'Completed'")
    Double findAverageScoreByUser(@Param("user") User user);
    Long countByUser(User user);

}
