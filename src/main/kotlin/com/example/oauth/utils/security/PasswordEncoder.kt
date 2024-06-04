package com.example.oauth.utils.security

import org.springframework.stereotype.Component
import java.security.MessageDigest
import java.util.UUID

@Component
class PasswordEncoder {
    fun encode(
        target: String,
        salt: String = UUID.randomUUID().toString(),
    ): String {
        return "$salt||" + MessageDigest
            .getInstance("SHA-256")
            .digest((salt + target).toByteArray())
            .fold("") { str, it -> str + "%02x".format(it) }
    }

    fun matches(target: String, hashed: String): Boolean {
        val salt = hashed.split("||")[0]
        return encode(target, salt) == hashed
    }
}
