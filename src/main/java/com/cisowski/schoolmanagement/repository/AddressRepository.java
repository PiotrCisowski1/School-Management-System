package com.cisowski.schoolmanagement.repository;

import com.cisowski.schoolmanagement.model.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}
