package goormthonuniv.swu.starcapsule.user;

import goormthonuniv.swu.starcapsule.global.config.jwt.TokenProvider;
import goormthonuniv.swu.starcapsule.refreshToken.RefreshToken;
import goormthonuniv.swu.starcapsule.refreshToken.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Transactional
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;

    public User findByAccessToken(String token) {

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        if (!tokenProvider.validToken(token)) {
            // Access Token이 만료된 경우, Refresh Token으로 새로운 Access Token 발급 시도
            RefreshToken refreshToken = tokenProvider.getStoredRefreshToken(token);

            if (refreshToken != null && tokenProvider.validRefreshToken(String.valueOf(refreshToken))) {
                // Refresh Token이 유효하다면 새로운 Access Token 발급
                String newAccessToken = tokenProvider.refreshAccessToken(String.valueOf(refreshToken));

                // 발급된 새로운 Access Token으로 인증 객체 생성
                Authentication authentication = tokenProvider.getAuthentication(newAccessToken);

                return (User) authentication.getPrincipal();
            } else {
                // Refresh Token도 유효하지 않으면 인증 실패 처리
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
            }
        }

        Authentication authentication = tokenProvider.getAuthentication(token);
        return (User) authentication.getPrincipal();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByNickname(String nickName) {
        return userRepository.findByNickname(nickName)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
