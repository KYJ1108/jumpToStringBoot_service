package com.example.add5.answer;

import com.example.add5.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {

    Page<Answer> findByQuestionId(Integer questionId, Pageable pageable);

    @Query("SELECT a FROM Answer a LEFT JOIN a.voter v WHERE a.question.id = :questionId GROUP BY a ORDER BY COUNT(v) DESC")
    Page<Answer> findByQuestionIdOrderedByVoteCount(@Param("questionId") Integer questionId, Pageable pageable);
}