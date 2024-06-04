package com.example.oauth.member.controller

import com.example.oauth.member.dto.IdResponseDto
import com.example.oauth.member.service.MemberService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/members")
@RestController
class MemberController(private val memberService: MemberService) {
    @GetMapping
    fun getMembers(): ResponseEntity<List<IdResponseDto>> {
        return ResponseEntity
            .status(HttpStatus.OK)
            .body(memberService.getMembers())
    }
}
