package goormthonuniv.swu.starcapsule.global.config.jwt;

import goormthonuniv.swu.starcapsule.refreshToken.RefreshToken;
import goormthonuniv.swu.starcapsule.refreshToken.RefreshTokenRepository;
import goormthonuniv.swu.starcapsule.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.Collections;
import java.util.Date;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class TokenProvider {

    private final JwtProperties jwtProperties;
    private final RefreshTokenProvider refreshTokenProvider;
    private final AccessTokenProvider accessTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    public String generateToken(User user, Duration expiredAt) {
        Date now = new Date();
        return makeToken(new Date(now.getTime() + expiredAt.toMillis()), user);
    }

    //JWT token 생성 메서드
    private String makeToken(Date expiry, User user) {
        Date now = new Date();

        return Jwts.builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(now)
                .setExpiration(expiry)
                .setSubject(user.getEmail())
                .claim("id", user.getId())
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecretKey())
                .compact();
    }

    //JWT token 유효성 검증 메서드
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .setSigningKey(jwtProperties.getSecretKey())
                    .parseClaimsJws(token);

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    //Token 기반으로 인증정보를 가져오는 메서드
    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);
        Set<SimpleGrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        User user = getUserFromClaims(claims);

        return new UsernamePasswordAuthenticationToken(user, token, authorities);
    }

    private User getUserFromClaims(Claims claims) {
        // Claims에서 사용자 정보를 추출하고 User 객체를 생성하거나 조회하는 로직 구현
        String email = claims.getSubject();
        // 예시: 사용자 정보를 데이터베이스에서 조회
        return new User(email);
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody();
    }


    public RefreshToken getStoredRefreshToken(String token) {
        // Access Token에 연결된 Refresh Token을 데이터베이스나 캐시에서 조회
        return refreshTokenRepository.findRefreshTokenByRefreshToken(token).orElse(null);
    }

    public boolean validRefreshToken(String refreshToken) {
        // Refresh Token이 유효한지 확인합니다. 예를 들어, 만료 여부를 확인 가능
        return refreshTokenProvider.isValid(refreshToken);
    }

    public String refreshAccessToken(String refreshToken) {
        // 유효한 Refresh Token이 주어지면 새로운 Access Token을 생성하여 반환
        if (validRefreshToken(refreshToken)) {
            // Refresh Token에서 사용자 정보를 추출하여 새로운 Access Token을 생성
            String userId = refreshTokenProvider.getUserIdFromToken(refreshToken);
            return accessTokenProvider.createToken(userId);
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }
    }

}
