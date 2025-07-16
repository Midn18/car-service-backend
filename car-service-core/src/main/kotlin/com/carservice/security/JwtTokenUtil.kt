package com.carservice.security

import com.carservice.config.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys.hmacShaKeyFor
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtTokenUtil(val jwtProperties: JwtProperties) {

    fun generateToken(username: String, claims: Map<String, Any>): String =
        Jwts.builder()
            .setSubject(username)
            .addClaims(claims)
            .setExpiration(Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration))
            .signWith(
                hmacShaKeyFor(jwtProperties.key.toByteArray()),
                SignatureAlgorithm.HS512
            ).compact()

    private fun getClaims(token: String) =
        Jwts.parserBuilder()
            .setSigningKey(hmacShaKeyFor(jwtProperties.key.toByteArray()))
            .build()
            .parseClaimsJws(token)
            .body

    fun getEmail(token: String): String = getClaims(token).subject

    fun isTokenValid(token: String): Boolean {
        val claims = getClaims(token)
        val expirationDate = claims.expiration
        val now = Date(System.currentTimeMillis())
        return now.before(expirationDate)
    }
}