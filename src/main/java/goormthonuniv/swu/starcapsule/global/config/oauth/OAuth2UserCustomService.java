package goormthonuniv.swu.starcapsule.global.config.oauth;

import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class OAuth2UserCustomService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest); // ❶ 요청을 바탕으로 유저 정보를 담은 객체 반환

        saveOrUpdateKakaoUser(oAuth2User);

        return oAuth2User;
    }

    private User saveOrUpdateKakaoUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Kakao OAuth 사용자 정보에서 필요한 정보 추출
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String email = (String) kakaoAccount.get("email");
        String nickname = (String) profile.get("nickname");

        User user = userRepository.findByEmail(email)
                .orElse(new User(email,nickname));

        return userRepository.save(user);
    }
}
