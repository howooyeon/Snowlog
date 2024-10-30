package goormthonuniv.swu.starcapsule.memory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemoryRepository extends JpaRepository<Memory, Long> {
    boolean existsBySnowball_IdAndCreatedAtBetween(Long snowballId, LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT m FROM Memory m WHERE m.createdAt >= :startDateTime AND m.createdAt < :endDateTime AND m.snowball.id = :snowballId")
    List<Memory> findMemoriesByCreatedAtBetweenAndSnowballId(
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            @Param("snowballId") Long snowballId);

}

