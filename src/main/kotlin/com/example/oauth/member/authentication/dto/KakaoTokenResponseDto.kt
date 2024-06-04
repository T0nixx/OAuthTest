package com.example.oauth.member.authentication.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoTokenResponseDto(
    @JsonProperty("access_token")
    val kakaoAccessToken: String,
    @JsonProperty("id_token")
    val idToken: String,
    @JsonProperty("token_type")
    val tokenType: String,
    @JsonProperty("expires_in")
    val expiresIn: Int,
    @JsonProperty("refresh_token")
    val refreshToken: String,
    @JsonProperty("refresh_token_expires_in")
    val refreshTokenExpiresIn: Int,
    val scope: String?,
)
