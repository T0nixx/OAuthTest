package com.example.oauth.member.authentication

data class SignInRequestDto(
    val email: String,
    val password: String,
)
