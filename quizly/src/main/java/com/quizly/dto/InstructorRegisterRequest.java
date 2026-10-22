package com.quizly.dto;

public record InstructorRegisterRequest(String name, String email, String password, String inviteCode) {
}
