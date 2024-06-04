package com.example.oauth.member.authentication.service

import com.example.oauth.member.authentication.dto.KakaoIdTokenPayload
import com.example.oauth.member.authentication.dto.KakaoTokenResponseDto
import com.example.oauth.member.authentication.dto.LoginResponseDto
import com.example.oauth.member.authentication.dto.OAuthLoginResponseDto
import com.example.oauth.member.authentication.dto.SignInRequestDto
import com.example.oauth.member.authentication.dto.SignUpRequestDto
import com.example.oauth.member.dto.MemberResponse
import com.example.oauth.member.model.SocialProvider
import com.example.oauth.member.repository.MemberRepository
import com.example.oauth.utils.security.KakaoOauthProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.Base64

private const val STATE = "KAKAO-OAUTH-STATE"

@Service
class KakaoOAuthServiceImpl(
    private val kakaoOauthProperties: KakaoOauthProperties,
    private val authService: AuthService,
    private val memberRepository: MemberRepository,
) :
    OAuthService {
    override fun getAuthorizeUrl(): String {
        val (clientId, redirectUri, baseUrl) = kakaoOauthProperties
        val uriComponents =
            UriComponentsBuilder.fromUriString("${baseUrl}/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", clientId)
                .queryParam(
                    "redirect_uri",
                    URLEncoder.encode(redirectUri, "UTF-8"),
                )
                .queryParam("state", URLEncoder.encode(STATE, "UTF-8"))
                .build()
        return uriComponents.toString()
    }

    override fun signInWithOauth(
        state: String,
        code: String,
    ): OAuthLoginResponseDto {
        if (state != STATE) throw IllegalStateException("state from OAuth Provider is not valid")
        val (clientId, _, baseUrl) = kakaoOauthProperties
        val uriComponents =
            UriComponentsBuilder.fromUriString("${baseUrl}/token")
                .queryParam("client_id", clientId)
                .queryParam("grant_type", "authorization_code")
                .queryParam("state", URLEncoder.encode(STATE, "UTF-8"))
                .queryParam("code", code)
                .build()
        val getTokenUrl = uriComponents.toUriString()
        val tokenRequest =
            HttpRequest.newBuilder().uri(URI.create(getTokenUrl)).GET().build()
        val tokenResponse =
            HttpClient
                .newHttpClient()
                .send(tokenRequest, HttpResponse.BodyHandlers.ofString())

        val (kakaoAccessToken, idToken) =
            jacksonObjectMapper()
                .readValue<KakaoTokenResponseDto>(tokenResponse.body())
        val payload = idToken.split(".")[1]
        val (kakaoId, nickname) = jacksonObjectMapper().readValue<KakaoIdTokenPayload>(
            Base64
                .getDecoder()
                .decode(payload),
        )
        // email을 받을 수 없어 임시 이메일로 대체
        val email = "$kakaoId@daum.net"
        if (this.checkSignedUp(email) == false) {
            this.signUp(
                SignUpRequestDto(
                    email = email,
                    nickname = nickname,
                    password = kakaoId,
                ),
            )
        }
        val (accessToken) =
            this.signIn(
                SignInRequestDto(
                    email = email,
                    password = kakaoId,
                ),
            )

        return OAuthLoginResponseDto(
            accessToken = accessToken,
            oAuthProviderAccessToken = kakaoAccessToken,
        )
    }

    private fun signUp(request: SignUpRequestDto): MemberResponse {
        return authService.signUp(request, SocialProvider.KAKAO)
    }

    private fun signIn(request: SignInRequestDto): LoginResponseDto {
        return authService.signIn(request)
    }

    private fun checkSignedUp(email: String): Boolean {
        return memberRepository.existsByEmail(email)
    }
}

