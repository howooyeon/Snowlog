package goormthonuniv.swu.starcapsule.memory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class MemoryObjectShapeService {
    private final MemoryObjectShapeRepository memoryObjectShapeRepository;
    public MemoryObjectShape findById(Long id) {
        return memoryObjectShapeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected memory object"));
    }

    public MemoryObjectShape findByObjectShapeName(String objectShapeName) {
        return memoryObjectShapeRepository.findByObjectShapeName(objectShapeName)
                .orElseThrow(() -> new IllegalArgumentException("Unexpected memory object"));
    }
}
