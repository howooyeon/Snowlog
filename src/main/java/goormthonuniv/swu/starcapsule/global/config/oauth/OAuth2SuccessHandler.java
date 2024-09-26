package goormthonuniv.swu.starcapsule.global.config.oauth;

import goormthonuniv.swu.starcapsule.global.config.cookie.CookieUtil;
import goormthonuniv.swu.starcapsule.global.config.jwt.TokenProvider;
import goormthonuniv.swu.starcapsule.refreshToken.RefreshToken;
import goormthonuniv.swu.starcapsule.refreshToken.RefreshTokenRepository;
import goormthonuniv.swu.starcapsule.snowball.SnowballService;
import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserRepository;
import goormthonuniv.swu.starcapsule.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String MAKE_SNOWBALL_REDIRECT_PATH = "http://localhost:3000/";
    public static final String MY_SNOWBALL_REDIRECT_PATH = "http://localhost:3000/";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepository;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String nickname, email;

        nickname = getKakaoNickname(oAuth2User);
        email = getKakaoEmail(oAuth2User);

        User user = userService.findByEmail(email);

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user, refreshToken);
        addRefreshTokenToCookie(request, response, refreshToken);

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);

        String targetUrl = "";

        if(user.getSnowball() == null){
            targetUrl = getMakeSnowballPageUrl(accessToken);
        }else{
            targetUrl = getMySnowballPageUrl(accessToken);
        }

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }

    private String getKakaoEmail(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");

        String email = (String) kakaoAccount.get("email");
        return email;
    }

    private String getKakaoNickname(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

        String nickname = (String) profile.get("nickname");
        return nickname;
    }

    @Transactional
    private void saveRefreshToken(User user, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepository.findByUser(user)
                .map(entity -> entity.update(newRefreshToken))
                .orElse(new RefreshToken(user, newRefreshToken));

        refreshTokenRepository.save(refreshToken);
        user.setRefreshToken(refreshToken);
        userRepository.save(user);

    }

    private void addRefreshTokenToCookie(HttpServletRequest request, HttpServletResponse response, String refreshToken) {
        int cookieMaxAge = (int) REFRESH_TOKEN_DURATION.toSeconds();

        CookieUtil.deleteCookie(request, response, REFRESH_TOKEN_COOKIE_NAME);
        CookieUtil.addCookie(response, REFRESH_TOKEN_COOKIE_NAME, refreshToken, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

    private String getMakeSnowballPageUrl(String token) {
        return UriComponentsBuilder.fromUriString(MAKE_SNOWBALL_REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }

    private String getMySnowballPageUrl(String token) {
        return UriComponentsBuilder.fromUriString(MY_SNOWBALL_REDIRECT_PATH)
                .queryParam("token", token)
                .build()
                .toUriString();
    }
}

