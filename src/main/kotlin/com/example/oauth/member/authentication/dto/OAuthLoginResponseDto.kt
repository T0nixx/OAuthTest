package com.example.oauth.member.authentication.dto

data class OAuthLoginResponseDto(
    val accessToken: String,
    val oAuthProviderAccessToken: String,
)
