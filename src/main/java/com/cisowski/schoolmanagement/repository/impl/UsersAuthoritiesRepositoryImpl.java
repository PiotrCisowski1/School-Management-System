package com.cisowski.schoolmanagement.repository.impl;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.repository.UsersAuthoritiesRepository;
import com.cisowski.schoolmanagement.utility.DbLogger;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.HashSet;

@Repository
public class UsersAuthoritiesRepositoryImpl implements UsersAuthoritiesRepository {

    @PersistenceContext
    private EntityManager entityManager;
    private Logger logger;
    @Transactional
    @Override
    public void addUserAuthorities(HashSet<Authority> authorities, User user) {
        logger = DbLogger.getLogger();
        String message = String.format("Starting inserting User's authorities for UserID: %s, authorities: %s",user.getId(), authorities.toString());
        logger.info(DbLogger.buildInfoMessage(message));

        authorities.forEach(authority ->{
            String sql = "INSERT INTO users_authorities values(?, ?)";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1, user.getId());
            query.setParameter(2, authority.getId());
            query.executeUpdate();
        });
        message = "Authorities inserted successfully for UserID: "+user.getId();
        logger.info(DbLogger.buildInfoMessage(message));
    }
}
