package org.example.ticketingdemo.domain.auth.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;

@Component
public class JwtProvider {

    private static final long JWT_EXPIRATION = 1000 * 60 * 60; //  JWT 만료 시간 1시간

    @Value("${jwt.secret.key}") // yml에서 secretKey 주입
    private String secretKey;


    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey); // yml에서 불러온 키를 Base64로 디코딩
        return Keys.hmacShaKeyFor(keyBytes); // HMAC-SHA 키 생성
    }

    // JWT 토큰 생성
    public String createToken(String subject) {
        Date now = new Date(); // 현재 시간
        Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);// 만료 시간 계산

        return Jwts.builder()
                .setSubject(subject) // 토큰 소유자
                .setIssuedAt(now) // 발급 시간
                .setExpiration(expiryDate) // 만료 시간
                .signWith(getSigningKey(), SignatureAlgorithm.HS512) // 서명
                .compact(); // 토큰 생성
    }

    // 토큰에서 Authentication 객체 생성
    public Authentication getAuthentication(String token) {
        String username = getClaims(token).getSubject();  // 토큰에서 username 추출


        // User 객체 생성
        User principal = new User(username, "", new ArrayList<>()); // User 객체 생성, 권한은 빈 리스트
        return new UsernamePasswordAuthenticationToken(principal, token, principal.getAuthorities()); // 인증 객체 반환
    }


    // 토큰 유효성  검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // 서명키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 파싱
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 토큰에서 Claims 추출
    // Claims: JWT 안에 담긴 실제 데이터
    // JWT 검증 후에 내부 정보를 안전하게 읽기 위해
    private Claims getClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
