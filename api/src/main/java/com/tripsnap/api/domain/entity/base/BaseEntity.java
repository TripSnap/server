package com.tripsnap.api.domain.entity.base;


import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate
    private LocalDateTime createdAt;
    @CreatedDate
    private LocalDateTime updatedAt;
}
