package goormthonuniv.swu.starcapsule.myMemory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MyMemoryShapeRepository extends JpaRepository<MyMemoryShape, Long> {
    Optional<MyMemoryShape> findByName(String name);
}