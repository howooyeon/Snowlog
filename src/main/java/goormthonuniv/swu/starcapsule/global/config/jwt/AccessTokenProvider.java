package goormthonuniv.swu.starcapsule.global.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class AccessTokenProvider {

    @Value("${jwt.secret_key}") // 애플리케이션 설정에 저장된 비밀 키
    private String secretKey;

    @Value("${jwt.access-token-expiration}") // Access Token 만료 시간 (밀리초 단위로 설정)
    private long accessTokenExpiration;

    public String createToken(String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    /**
     * Access Token 유효성 검사 메서드
     *
     * @param token 검증할 Access Token
     * @return 유효한 경우 true 반환, 유효하지 않으면 false 반환
     */
    public boolean isValid(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token); // 유효성 검증

            return true;
        } catch (Exception e) {
            System.out.println("Invalid or expired access token");
            return false;
        }
    }

    /**
     * Access Token에서 사용자 ID 추출 메서드
     *
     * @param token 사용자 ID를 추출할 Access Token
     * @return 사용자 ID (토큰이 유효하지 않으면 null 반환)
     */
    public String getUserIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();

            return claims.getSubject(); // subject에서 사용자 ID를 추출
        } catch (Exception e) {
            System.out.println("Could not extract user ID from token");
            return null;
        }
    }
}
