package com.example.oauth.member.authentication

data class SignUpRequestDto(
    val email: String,
    val password: String,
    val nickname: String,
)
