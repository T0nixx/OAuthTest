package com.example.oauth.member.authentication.controller

import com.example.oauth.member.authentication.SignInRequestDto
import com.example.oauth.member.authentication.SignUpRequestDto
import com.example.oauth.member.dto.IdResponseDto
import com.example.oauth.member.service.AuthService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/auth")
@RestController
class AuthController(private val authService: AuthService) {
    @PostMapping("/sign-up")
    fun signUp(
        @RequestBody
        signUpRequestDto: SignUpRequestDto,
    ): ResponseEntity<IdResponseDto> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.signUp(signUpRequestDto))
    }

    @PostMapping("/sign-in")
    fun signIn(
        @RequestBody
        signInRequestDto: SignInRequestDto,
    ): ResponseEntity<IdResponseDto> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.signIn(signInRequestDto))
    }
}
