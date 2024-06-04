package com.example.oauth.member.authentication.controller

import com.example.oauth.member.authentication.dto.LoginResponseDto
import com.example.oauth.member.authentication.dto.SignInRequestDto
import com.example.oauth.member.authentication.dto.SignUpRequestDto
import com.example.oauth.member.authentication.service.AuthService
import com.example.oauth.member.dto.MemberResponse
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
    ): ResponseEntity<MemberResponse> {
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(authService.signUp(signUpRequestDto))
    }

    @PostMapping("/sign-in")
    fun signIn(
        @RequestBody
        signInRequestDto: SignInRequestDto,
    ): ResponseEntity<LoginResponseDto> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(authService.signIn(signInRequestDto))
    }
}
