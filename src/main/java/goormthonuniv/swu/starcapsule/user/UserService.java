package goormthonuniv.swu.starcapsule.user;

import goormthonuniv.swu.starcapsule.global.config.jwt.TokenProvider;
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
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
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
