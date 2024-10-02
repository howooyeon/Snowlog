package goormthonuniv.swu.starcapsule.memory;

import goormthonuniv.swu.starcapsule.snowball.Snowball;
import goormthonuniv.swu.starcapsule.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoryObjectShapeRepository extends JpaRepository<MemoryObjectShape, Long> {
    Optional<MemoryObjectShape> findById(Long id);
}
