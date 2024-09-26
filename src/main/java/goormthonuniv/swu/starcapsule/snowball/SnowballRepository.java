package goormthonuniv.swu.starcapsule.snowball;

import goormthonuniv.swu.starcapsule.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SnowballRepository extends JpaRepository<Snowball, Long> {
    Optional<Snowball> findByUser(User user);
}
