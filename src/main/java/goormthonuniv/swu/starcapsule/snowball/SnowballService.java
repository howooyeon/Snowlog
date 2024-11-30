package goormthonuniv.swu.starcapsule.snowball;

import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SnowballService {
    private final UserService userService;
    private final SnowballRepository snowballRepository;

    @Transactional
    public Snowball makeSnowball(String email) {
        User user = userService.findByEmail(email);

        if (user.getSnowball() != null) {
            throw new IllegalArgumentException("이미 생성된 스노우볼이 있습니다.");
        }
        String sharedLink = makeShareLink();
        String uid = sharedLink.substring(sharedLink.lastIndexOf("/") + 1);

        Snowball snowball = new Snowball(user.getNickname(), sharedLink, user);
        snowball.setUid(uid);
        snowballRepository.save(snowball);

        user.setSnowball(snowball);

        return snowball;
    }

    public Snowball getSnowball(String id) {
        String link = "https://develop.snowlog.pages.dev/main/" + id;
        return snowballRepository.findBySharedLink(link)
                .orElseThrow(() -> new IllegalArgumentException("해당 id의 capsule이 없습니다."));
    }

    public Snowball getSnowballByUserEmail(String email) {
        return snowballRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 capsule이 없습니다."));
    }

    public Snowball getMySnowball(String id, String email) {
        Snowball snowball = getSnowball(id);

        Snowball snowballGetByUser = snowballRepository.findByUserEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자의 capsule이 없습니다."));

        if (!snowball.getSharedLink().equals(snowballGetByUser.getSharedLink())) {
            throw new IllegalArgumentException("해당 사용자의 capsule이 아닙니다.");
        }
        return snowball;
    }

    @Transactional
    public Snowball changeSnowballName(String email, String snowballName) {
        User user = userService.findByEmail(email);

        Snowball snowball = user.getSnowball();
        snowball.updateSnowballName(snowballName);

        return snowball;
    }

    public String makeShareLink() {
        return "https://develop.snowlog.pages.dev/main/" + UUID.randomUUID().toString();
    }

    public Snowball findBySharedLink(String id) {
        return snowballRepository.findBySharedLink(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "id에 해당하는 snowball이 없습니다."));
    }

}
