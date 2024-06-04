package com.example.oauth.member.controller

import com.example.oauth.member.dto.IdResponseDto
import com.example.oauth.member.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/members")
@RestController
class MemberController(private val memberService: MemberService) {
    @GetMapping
    fun getMembers(
        @RequestHeader("Authorization")
        accessToken: String,
    ): ResponseEntity<List<IdResponseDto>> {
        return try {
            ResponseEntity
                .status(HttpStatus.OK)
                .body(memberService.getMembers(accessToken))
        } catch (e: IllegalStateException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        } catch (e: MissingRequestHeaderException) {
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
    }
}
