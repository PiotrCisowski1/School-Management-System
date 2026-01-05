package com.cisowski.schoolmanagement.users.common.repository;

import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<AuthorityEntity, Integer> {

    List<AuthorityEntity> findByAuthorityIn(List<String> authorities);

}
