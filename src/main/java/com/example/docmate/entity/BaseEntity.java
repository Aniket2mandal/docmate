package com.example.docmate.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


@Getter
@Setter
@MappedSuperclass
public class BaseEntity implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public BaseEntity() {
        this.createdDateTime = this.updatedDateTime = LocalDateTime.now();
    }

    @Id
//    @GeneratedValue(generator = "uuid2")
    @GeneratedValue(strategy = GenerationType.UUID)

    @Column(length = 36, name = "id")
    protected String id;

    @Column(name = "created_datetime")
    protected LocalDateTime createdDateTime;

    @Column(name = "updated_datetime")
    protected LocalDateTime updatedDateTime;

    @PreUpdate
    protected void onUpdate() {
        this.updatedDateTime = LocalDateTime.now();
    }
}
