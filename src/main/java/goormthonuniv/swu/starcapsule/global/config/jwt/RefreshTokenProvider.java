package goormthonuniv.swu.starcapsule.global.config.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenProvider {

    @Value("${jwt.secret_key}")
    private String secretKey;

    /**
     * Refresh Token 유효성 검사 메서드
     *
     * @param refreshToken 검증할 Refresh Token
     * @return 유효한 경우 true 반환, 유효하지 않은 경우 false 반환
     */
    public boolean isValid(String refreshToken) {
        try {
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(refreshToken); // 유효성 검증

            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            System.out.println("Invalid JWT signature or format");
        } catch (ExpiredJwtException e) {
            System.out.println("Expired JWT token");
        }
        return false;
    }

    /**
     * Refresh Token에서 사용자 ID 추출 메서드
     *
     * @param refreshToken 사용자 ID를 추출할 Refresh Token
     * @return 사용자 ID (토큰이 유효하지 않으면 null 반환)
     */
    public String getUserIdFromToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(refreshToken)
                    .getBody();

            return claims.getSubject(); // 사용자 ID를 subject에서 추출
        } catch (Exception e) {
            System.out.println("Could not extract user ID from token");
            return null;
        }
    }
}
