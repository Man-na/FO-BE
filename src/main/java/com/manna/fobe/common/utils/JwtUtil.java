package com.manna.fobe.common.utils;

import com.manna.fobe.common.exception.BizRuntimeException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j(topic = "JwtUtil")
@Component
public class JwtUtil {

    public static final String BEARER_PREFIX = "Bearer ";

    private static final long ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000L;       // 1시간
    private static final long REFRESH_TOKEN_EXPIRE_TIME = 7 * 24 * 60 * 60 * 1000L; // 7일

    @Value("${jwt.secret.key}")
    private String secretKey;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        this.key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * AccessToken 생성
     */
    public String createToken(int userId, String role) {
        return generateToken(userId, role, ACCESS_TOKEN_EXPIRE_TIME);
    }

    /**
     * RefreshToken 생성
     */
    public String createRefreshToken(int userId, String role) {
        return generateToken(userId, role, REFRESH_TOKEN_EXPIRE_TIME);
    }

    /**
     * 실제 JWT 토큰을 만드는 로직
     */
    private String generateToken(int userId, String role, long expireTime) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expireTime);

        // claim에 넣을 정보
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("userId", userId);

        return Jwts.builder()
                        .setSubject(String.valueOf(userId))
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(expiration)
                        .signWith(key, signatureAlgorithm)
                        .compact();
    }

    /**
     * 토큰 검증
     * - 형식(Bearer ) 체크
     * - 서명, 만료시간 등 검증
     */
    public boolean validateToken(String token) {
        try {
            // "Bearer " 접두사 확인
            if (!token.startsWith(BEARER_PREFIX)) {
                throw new BizRuntimeException("잘못된 토큰 형식입니다. (Bearer 누락)");
            }

            // "Bearer " 제거
            token = token.substring(BEARER_PREFIX.length());

            // 파싱 단계에서 서명 검증 & 만료 검사
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT Signature: {}", e.getMessage());
            throw new BizRuntimeException("Invalid JWT Signature");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT Token: {}", e.getMessage());
            throw new BizRuntimeException("Expired JWT Token");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT Token: {}", e.getMessage());
            throw new BizRuntimeException("Unsupported JWT Token");
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new BizRuntimeException("JWT claims string is empty");
        } catch (BizRuntimeException e) {
            throw e;
        }
    }

    /**
     * 토큰에서 userId 추출
     */
    public int getUserIdFromToken(String token) {
        // "Bearer " 제거 후 Claims 파싱
        Claims claims = parseClaims(token);
        return claims.get("userId", Integer.class).intValue();
    }

    /**
     * 토큰에서 role 추출
     */
    public String getRoleFromToken(String token) {
        Claims claims = parseClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * "Bearer " 제거 후 Claims 파싱
     */
    private Claims parseClaims(String token) {
        if (token.startsWith(BEARER_PREFIX)) {
            token = token.substring(BEARER_PREFIX.length());
        }
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
