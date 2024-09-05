package com.cisowski.schoolmanagement.repository.impl;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.UsersAuthoritiesRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
public class UsersAuthoritiesRepositoryImpl implements UsersAuthoritiesRepository {

    @PersistenceContext
    private EntityManager entityManager;
    @Transactional
    @Override
    public void addUserAuthorities(HashSet<Authority> authorities, User user) {
        authorities.forEach(authority ->{
            String sql = "INSERT INTO users_authorities values(?, ?)";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, user.getId());
            query.setParameter(2, authority.getId());
            query.executeUpdate();
        });
    }
}
