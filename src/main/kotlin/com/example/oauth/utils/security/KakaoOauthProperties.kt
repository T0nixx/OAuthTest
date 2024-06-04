package com.example.oauth.utils.security

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
data class KakaoOauthProperties(
    @Value("\${kakao-oauth.clientId}")
    val clientId: String,
    @Value("\${kakao-oauth.redirectUri}")
    val redirectUri: String,
    @Value("\${kakao-oauth.baseUrl}")
    val baseUrl: String,
)
