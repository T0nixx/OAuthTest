package com.example.oauth.member.service

import com.example.oauth.member.dto.IdResponseDto
import com.example.oauth.member.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val memberRepository: MemberRepository,
) {
    fun getMembers(): List<IdResponseDto> {
        return memberRepository.findAll().map { IdResponseDto(id = it.id!!) }
    }
}
