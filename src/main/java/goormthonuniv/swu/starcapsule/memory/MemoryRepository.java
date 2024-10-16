package goormthonuniv.swu.starcapsule.memory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    boolean existsBySnowball_IdAndCreatedAtBetween(Long snowballId, LocalDateTime startOfDay, LocalDateTime endOfDay);
}

