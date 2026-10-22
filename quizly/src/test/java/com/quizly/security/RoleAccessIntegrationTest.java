package com.quizly.security;

import com.quizly.QuizlyApplication;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = QuizlyApplication.class, properties = {
        "spring.datasource.url=jdbc:h2:mem:quiz-platform-access-tests;DB_CLOSE_DELAY=-1",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect",
        "app.auth.instructor-invite-code=test-instructor-invite",
        "JWT_SECRET=test-signing-key-with-at-least-32-bytes-long"
})
@AutoConfigureMockMvc
class RoleAccessIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void studentsAndInstructorsHaveSeparatePermissionsAndOwnedResources() throws Exception {
        String studentToken = registerStudent("learner@example.com");
        String instructorToken = registerInstructor("teacher@example.com");
        String otherInstructorToken = registerInstructor("other-teacher@example.com");

        mockMvc.perform(post("/auth/instructor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"No Invite","email":"no-invite@example.com",
                                 "password":"long-password","inviteCode":"wrong"}
                                """))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/quizzes/mine").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/quizzes")
                        .header("Authorization", bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Student Quiz","category":"Demo",
                                 "timeLimitMinutes":10,"instructorId":1}
                                """))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/attempts/submit")
                        .header("Authorization", bearer(instructorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"quizId\":1,\"answers\":{}}"))
                .andExpect(status().isForbidden());

        String quizResponse = mockMvc.perform(post("/quizzes")
                        .header("Authorization", bearer(instructorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Owned Quiz","category":"Demo",
                                 "timeLimitMinutes":10,"instructorId":999}
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long quizId = objectMapper.readTree(quizResponse).get("id").asLong();
        mockMvc.perform(post("/questions")
                        .header("Authorization", bearer(instructorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "quizId", quizId,
                                "questionText", "Which option is correct?",
                                "optionA", "A",
                                "optionB", "B",
                                "optionC", "C",
                                "optionD", "D",
                                "correctOption", "B"))))
                .andExpect(status().isOk());

        mockMvc.perform(get("/quizzes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(4));
        mockMvc.perform(get("/questions/quiz/{quizId}/answer-key", quizId)
                        .header("Authorization", bearer(otherInstructorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionText").value("Which option is correct?"))
                .andExpect(jsonPath("$[0].optionB").value("B"))
                .andExpect(jsonPath("$[0].correctOption").value("B"));

        mockMvc.perform(delete("/quizzes/{id}", quizId)
                        .header("Authorization", bearer(otherInstructorToken)))
                .andExpect(status().isForbidden());

        JsonNode questions = objectMapper.readTree(mockMvc.perform(get("/questions/quiz/1"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString());
        long seededQuestionId = questions.get(0).get("id").asLong();
        mockMvc.perform(get("/questions/quiz/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correctOption").value(nullValue()));

        String attemptResponse = mockMvc.perform(post("/attempts/submit")
                        .header("Authorization", bearer(studentToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "studentName", "Forged Name",
                                "quizId", 1,
                                "answers", Map.of(Long.toString(seededQuestionId), "C")))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentName").value("Learner"))
                .andReturn().getResponse().getContentAsString();
        long attemptId = objectMapper.readTree(attemptResponse).get("id").asLong();

        mockMvc.perform(get("/results/attempt/{id}", attemptId)
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].questionText").exists())
                .andExpect(jsonPath("$[0].optionC").exists())
                .andExpect(jsonPath("$[0].correctOption").exists())
                .andExpect(jsonPath("$[0].correct").exists());
        mockMvc.perform(get("/attempts/quiz/1")
                        .header("Authorization", bearer(instructorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentName").value("Learner"));
        mockMvc.perform(get("/attempts")
                        .header("Authorization", bearer(instructorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentName").value("Learner"));
        mockMvc.perform(get("/attempts")
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/results/analytics/1")
                        .header("Authorization", bearer(instructorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAttempts").value(1));
        mockMvc.perform(get("/questions/quiz/1/answer-key")
                        .header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/results/attempt/{id}", attemptId)
                        .header("Authorization", bearer(registerStudent("other-learner@example.com"))))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/attempts/me").header("Authorization", bearer(studentToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].studentName").value("Learner"));
        mockMvc.perform(get("/attempts/quiz/1").header("Authorization", bearer(studentToken)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/quizzes/mine").header("Authorization", bearer(instructorToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].instructorId").value(1));
    }

    private String registerStudent(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/student/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", email.startsWith("other") ? "Other Learner" : "Learner",
                                "email", email,
                                "password", "long-password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String registerInstructor(String email) throws Exception {
        MvcResult result = mockMvc.perform(post("/auth/instructor/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "name", "Teacher",
                                "email", email,
                                "password", "long-password",
                                "inviteCode", "test-instructor-invite"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("INSTRUCTOR"))
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
