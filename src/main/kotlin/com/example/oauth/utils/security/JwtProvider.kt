package com.example.oauth.utils.security

import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.Instant
import java.util.Date
import javax.crypto.spec.SecretKeySpec

@Component
class JwtProvider(
    @Value("\${jwt.secret}")
    private val secretKey: String,
) {
    val key = SecretKeySpec(secretKey.toByteArray(), "HmacSHA256")

    fun createToken(id: Long): String {
        return Jwts
            .builder()
            .signWith(key)
            .subject(id.toString())
            .issuer("OAUTH-TEST")
            .issuedAt(Date.from(Instant.now()))
            .expiration(Date.from(Instant.now().plusSeconds(86400)))
            .compact()!!
    }

    fun validateTokenAndGetSubject(token: String): Long {
        return Jwts
            .parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token).payload.subject.toLong()
    }

    fun parseToMemberId(bearerToken: String?): Long {
        println(bearerToken)
        val jwt =
            bearerToken
                .takeIf { it?.startsWith("Bearer ", true) ?: false }
                ?.substring(7)
        val subject = if (jwt == null) -1L
        else this.validateTokenAndGetSubject(jwt)

        return subject
    }
}

