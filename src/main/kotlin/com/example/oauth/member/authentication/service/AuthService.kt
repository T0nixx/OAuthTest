package com.example.oauth.member.service

import com.example.oauth.member.authentication.dto.SignInRequestDto
import com.example.oauth.member.authentication.dto.SignUpRequestDto
import com.example.oauth.member.dto.IdResponseDto
import com.example.oauth.member.model.Member
import com.example.oauth.member.repository.MemberRepository
import com.example.oauth.utils.security.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val memberRepository: MemberRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun signUp(request: SignUpRequestDto): IdResponseDto {
        val (email, password, nickname) = request
        return Member(
            email = email,
            password = passwordEncoder.encode(password),
            nickname = nickname,
            socialProvider = null,
        ).let {
            memberRepository.save(it)
        }.let { IdResponseDto(id = it.id!!) }

    }

    fun signIn(request: SignInRequestDto): IdResponseDto {
        val (email, password) = request
        val member =
            memberRepository.findByEmail(email)
                ?: throw IllegalArgumentException("Email or Password does not match.")
        if (passwordEncoder.matches(password, member.password) == false) {
            throw IllegalArgumentException("Email or Password does not match.")
        }

        return IdResponseDto(id = member.id!!)
    }
}
