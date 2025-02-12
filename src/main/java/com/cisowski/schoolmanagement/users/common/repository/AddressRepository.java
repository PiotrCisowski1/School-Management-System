package com.cisowski.schoolmanagement.users.common.repository;

import com.cisowski.schoolmanagement.users.common.model.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<AddressEntity, Integer> {
}
