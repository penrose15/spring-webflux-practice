package com.performance.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.performance.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.access.expiration}")
    private Long expiration;

    @Value("${jwt.access.header}")
    private String header;

    private static final String ACCESS_TOKEN_SUBJECT = "AccessToken";
    private static final String EMAIL_CLAIM = "email";




    public String createAccessToken(String email, String role) {
        Date now = new Date();

        return JWT.create()
                .withSubject(ACCESS_TOKEN_SUBJECT)
                .withExpiresAt(new Date(now.getTime() + expiration))
                .withClaim(EMAIL_CLAIM, email)
                .withClaim("role", role)
                .sign(Algorithm.HMAC512(secretKey));
    }

    public Authentication getAuthentication(String token) {
        DecodedJWT jwt = JWT.decode(token);
        String role = jwt.getClaim("role").asString();
        String email = jwt.getClaim("email").asString();

        CustomUserDetails userDetails = new CustomUserDetails(email, role, "");
        return new UsernamePasswordAuthenticationToken(userDetails, "", List.of(new SimpleGrantedAuthority(role)));
    }

    public boolean validateToken(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(token);
            return true;
        } catch (TokenExpiredException e) {
            log.error("token expired : {}", e.getMessage());
            return false;
        } catch (JWTVerificationException e) {
            log.error("jwt verification error = {}",e.getMessage());
        }
        return false;
    }

    public String getEmail(String accessToken) {
        DecodedJWT jwt = JWT.decode(accessToken);
        return jwt.getClaim(EMAIL_CLAIM).asString();
    }

    public boolean isTokenExpired(String token) {
        DecodedJWT jwt = JWT.decode(token);
        Date expDate = jwt.getExpiresAt();
        Date now = new Date();

        return now.after(expDate);
    }
}
