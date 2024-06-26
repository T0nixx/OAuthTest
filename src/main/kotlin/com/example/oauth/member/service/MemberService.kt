package com.example.oauth.member.service

import com.example.oauth.member.dto.MemberResponse
import com.example.oauth.member.repository.MemberRepository
import com.example.oauth.utils.security.JwtProvider
import org.springframework.stereotype.Service

@Service
class MemberService(
    private val jwtProvider: JwtProvider,
    private val memberRepository: MemberRepository,
) {
    fun getMembers(
        accessToken: String,
    ): List<MemberResponse> {
        val memberId = jwtProvider.parseToMemberId(accessToken)
        if (memberId == -1L || memberRepository.existsById(memberId) == false) {
            throw IllegalStateException("Unauthrized User")
        }


        return memberRepository
            .findAll()
            .map {
                MemberResponse(
                    id = it.id!!,
                    email = it.email,
                    nickname = it.nickname,
                )
            }
    }
}
