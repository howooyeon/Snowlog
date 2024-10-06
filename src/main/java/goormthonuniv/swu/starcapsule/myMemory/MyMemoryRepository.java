package goormthonuniv.swu.starcapsule.myMemory;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MyMemoryRepository extends JpaRepository<MyMemory, Long> {

    // 공개된 추억 페이징 조회
    Page<MyMemory> findByIsReleasedTrue(Pageable pageable);

    // 비공개 추억 페이징 조회
    Page<MyMemory> findByIsReleasedFalse(Pageable pageable);

    // 공개된 추억 개수
    int countByIsReleasedTrue();

    // 비공개 추억 개수
    int countByIsReleasedFalse();

}
