package com.cisowski.schoolmanagement.users.common.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "authorities")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthorityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = false)
    private String authority;


    @Override
    public String toString() {
        return "name=" + authority;
    }
}
