package com.cisowski.schoolmanagement.users.common.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity(name = "address")
@Data
public class AddressEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    @Column(nullable = false)
    String city;
    @Column(nullable = false)
    String street;
    @Column(nullable = false)
    String buildingNumber;
    @Column(nullable = false)
    String voivodeship;
    @Column(nullable = false)
    String zipCode;


    @Override
    public String toString() {
        return "Address{" +
                "id=" + id +
                ", city='" + city + '\'' +
                ", street='" + street + '\'' +
                ", buildingNumber='" + buildingNumber + '\'' +
                ", voivodeship='" + voivodeship + '\'' +
                ", zipCode='" + zipCode + '\'' +
                '}';
    }
}
