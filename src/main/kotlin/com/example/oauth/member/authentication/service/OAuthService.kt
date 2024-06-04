package com.example.oauth.member.authentication.service

import com.example.oauth.member.authentication.dto.OAuthLoginResponseDto

interface OAuthService {
    fun getAuthorizeUrl(): String
    fun signInWithOauth(state: String, code: String): OAuthLoginResponseDto
}

