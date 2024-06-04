package com.example.oauth.member.authentication.dto

data class OAuthSignUpRequestDto(
    val email: String,
    val password: String,
    val nickname: String,
    val socialProvider: String,
)

