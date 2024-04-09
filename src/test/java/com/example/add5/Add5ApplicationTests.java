package com.example.add5;

import com.example.add5.answer.AnswerService;
import com.example.add5.question.Question;
import com.example.add5.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class Add5ApplicationTests {

	@Autowired
	private QuestionService questionService;
	@Autowired
	private AnswerService answerService;

	@Test
	void testJpa() {
		for (int i = 1; i <= 30; i++) {
			String content = "내용무";
			Question question = this.questionService.getQuestion(300);
			this.answerService.create(question, content, null);
		}
	}
}
