package ru.agniaendie.authservicecorp.security.jwt

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import lombok.NonNull
import org.apache.tomcat.util.codec.binary.Base64
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import ru.agniaendie.authservicecorp.model.User
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import java.util.function.Function

@Service
class JwtTokenService(@Value("\${jwt.secret}")private var secret : String?) {
    fun validateToken(token: String): Boolean {
        return !isTokenExpired(token)
    }
    private fun getSigningKey(): String? {
        val keyBytes: ByteArray = secret!!.toByteArray(StandardCharsets.UTF_8)
        return Base64.encodeBase64String(keyBytes)
    }
    private fun isTokenExpired(token: String): Boolean {
        return extractExpiration(token).before(Date())
    }

    private fun extractExpiration(token: String): Date {
        return extractClaim(token) { obj: Claims? -> obj!!.expiration }
    }

    fun generateToken(user: User): String {
        val now = Date()
        val exp: Date = Date.from(
            LocalDateTime.now().plusMinutes(30).atZone(ZoneId.systemDefault()).toInstant()
        )
        return Jwts.builder().setExpiration(exp).setIssuedAt(now).setSubject(user.username).claim("uuid", user.uuid).claim("role",user.role)
            .signWith(SignatureAlgorithm.HS256, getSigningKey()).compact()
    }

    fun revokeToken(@NonNull token: String?) {}
    fun updateToken(@NonNull refreshToken: String?): String? {
        return ""
    }

    fun <T> extractClaim(token: String, claimsTFunction: Function<Claims?, T>): T {
        val claims: Claims = extractAllClaims(token)
        return claimsTFunction.apply(claims)
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser().setSigningKey(getSigningKey()).parseClaimsJws(token).body
    }
}
