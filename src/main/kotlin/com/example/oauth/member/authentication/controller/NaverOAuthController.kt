package com.example.oauth.member.authentication.controller

import com.example.oauth.member.authentication.dto.OAuthLoginResponseDto
import com.example.oauth.member.authentication.service.OAuthService
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/oauth2/naver")
@RestController
class NaverOAuthController(
    @Qualifier("naverOAuthServiceImpl")
    private val naverOAuthService: OAuthService,
) {
    @GetMapping("/login")
    fun login(): ResponseEntity<Unit> {
        val authorizeUrl = naverOAuthService.getAuthorizeUrl()

        return ResponseEntity
            .status(HttpStatus.MOVED_PERMANENTLY)
            .header("Location", authorizeUrl)
            .build()
    }

    @GetMapping("/success")
    fun success(
        @RequestParam
        state: String,
        @RequestParam
        code: String?,
        @RequestParam
        error: String?,
        @RequestParam(name = "error_description")
        errorDescription: String?,
    ): OAuthLoginResponseDto {
        if (error != null) throw IllegalStateException(errorDescription!!)
        return naverOAuthService.signInWithOauth(state, code!!)
    }
}
