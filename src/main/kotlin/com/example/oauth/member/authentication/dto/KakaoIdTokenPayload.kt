package com.example.oauth.member.authentication.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class KakaoIdTokenPayload(
    @JsonProperty("sub")
    val kakaoId: String,
    val nickname: String,
    @JsonProperty("auth_time")
    val authTime: Int,
    val iss: String,
    val exp: Int,
    val iat: Int,
    val aud: String,
    // 실제 서비스 하지 않으면 받기 힘든 것으로 보임
    // val email: String
)
