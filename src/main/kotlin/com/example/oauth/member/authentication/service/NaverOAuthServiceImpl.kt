package com.example.oauth.member.authentication.service

import com.example.oauth.member.authentication.dto.LoginResponseDto
import com.example.oauth.member.authentication.dto.NaverTokenResponseDto
import com.example.oauth.member.authentication.dto.NaverUserDataResponseDto
import com.example.oauth.member.authentication.dto.OAuthLoginResponseDto
import com.example.oauth.member.authentication.dto.SignInRequestDto
import com.example.oauth.member.authentication.dto.SignUpRequestDto
import com.example.oauth.member.dto.MemberResponse
import com.example.oauth.member.model.SocialProvider
import com.example.oauth.member.repository.MemberRepository
import com.example.oauth.utils.security.NaverOauthProperties
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

private const val STATE = "NAVER-OAUTH-STATE"

@Service
class NaverOAuthServiceImpl(
    private val naverOauthProperties: NaverOauthProperties,
    private val authService: AuthService,
    private val memberRepository: MemberRepository,
) :
    OAuthService {
    override fun getAuthorizeUrl(): String {
        val (clientId, _, redirectUri, baseUrl) = naverOauthProperties
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
        val (clientId, clientSecret, _, baseUrl) = naverOauthProperties
        val uriComponents =
            UriComponentsBuilder.fromUriString("${baseUrl}/token")
                .queryParam("client_id", clientId)
                .queryParam("client_secret", clientSecret)
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

        val naverAccessToken =
            jacksonObjectMapper().readValue<NaverTokenResponseDto>(tokenResponse.body()).naverAccessToken

        val naverUserDataUrl = "https://openapi.naver.com/v1/nid/me"

        val naverUserDataRequest =
            HttpRequest
                .newBuilder()
                .header("Authorization", "Bearer $naverAccessToken")
                .uri(URI.create(naverUserDataUrl))
                .GET()
                .build()
        val naverUserDataResponse =
            HttpClient
                .newHttpClient()
                .send(
                    naverUserDataRequest,
                    HttpResponse.BodyHandlers.ofString(),
                )

        val (id, email, _, nickname) = jacksonObjectMapper().readValue<NaverUserDataResponseDto>(
            naverUserDataResponse.body(),
        ).response

        if (this.checkSignedUp(email) == false) {
            this.signUp(
                SignUpRequestDto(
                    email = email,
                    password = id,
                    nickname = nickname,
                ),
            )
        }
        val (accessToken) =
            this.signIn(SignInRequestDto(email = email, password = id))

        return OAuthLoginResponseDto(
            accessToken = accessToken,
            oAuthProviderAccessToken = naverAccessToken,
        )
    }

    private fun signUp(request: SignUpRequestDto): MemberResponse {
        return authService.signUp(request, SocialProvider.NAVER)
    }

    private fun signIn(request: SignInRequestDto): LoginResponseDto {
        return authService.signIn(request)
    }

    private fun checkSignedUp(email: String): Boolean {
        return memberRepository.existsByEmail(email)
    }
}
