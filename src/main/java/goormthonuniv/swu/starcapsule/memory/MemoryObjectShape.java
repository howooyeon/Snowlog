package goormthonuniv.swu.starcapsule.memory;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "memory_object_shape")
public class MemoryObjectShape {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "object_name")
    private String objectShapeName;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "memoryObjectShape")
    private List<Memory> memories = new ArrayList<>();
}
