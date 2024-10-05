package goormthonuniv.swu.starcapsule.snowball;

import goormthonuniv.swu.starcapsule.user.User;
import goormthonuniv.swu.starcapsule.user.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class SnowballService {
    private final UserService userService;
    private final SnowballRepository snowballRepository;
    @Transactional
    public Snowball makeSnowball(String email){
        User user = userService.findByEmail(email);

        String sharedLink = makeShareLink();

        Snowball snowball = new Snowball(user.getNickname(), sharedLink, user);
        snowballRepository.save(snowball);

        user.setSnowball(snowball);

        return snowball;
    }
    public Snowball getSnowball(String id){
        String link = "http://localhost:3000/api/capsule/"+id;
        return snowballRepository.findBySharedLink(link)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected capsule"));
    }

    @Transactional
    public Snowball changeSnowballName(String email, String snowballName){
        User user = userService.findByEmail(email);

        Snowball snowball = user.getSnowball();
        snowball.updateSnowballName(snowballName);

        return snowball;
    }

    public String makeShareLink(){
        return "http://localhost:3000/api/capsule/" + UUID.randomUUID().toString();
    }

    public Snowball findBySharedLink(String id){
        return snowballRepository.findBySharedLink("http://localhost:3000/api/capsule/"+id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected snowball"));
    }
}
