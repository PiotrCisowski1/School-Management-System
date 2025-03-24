package com.cisowski.schoolmanagement.classroom.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "equipments")
public class Equipment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(nullable = false)
    private String name;
}
