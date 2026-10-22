package com.quizly;

import com.quizly.QuizlyApplication;
import com.quizly.repository.QuestionRepository;
import com.quizly.repository.QuizRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = QuizlyApplication.class, properties = {
		"spring.datasource.url=jdbc:h2:mem:quiz-platform-tests;DB_CLOSE_DELAY=-1",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
		"app.auth.instructor-invite-code=test-instructor-invite",
		"JWT_SECRET=test-signing-key-with-at-least-32-bytes-long"
})
class QuizlyApplicationTests {

	@Autowired
	private QuizRepository quizRepository;

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void contextLoads() {
		assertEquals(3, quizRepository.count());
		assertEquals(9, questionRepository.count());
	}
}
