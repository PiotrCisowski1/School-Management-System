package com.cisowski.schoolmanagement.users.common.service;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.common.repository.AuthorityRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    @Transactional
    public void resolveUserAuthorities(UserEntity user, Collection<AuthorityEntity> requestedAuthorities) {
        if(user == null || CollectionUtils.isEmpty(requestedAuthorities))
            return;
        DbLogger.info(String.format("Searching for Authorities for User with ID %s, authorities: %s", user.getId(), requestedAuthorities));
        List<String> authorityNames = requestedAuthorities.stream()
                .map(AuthorityEntity::getAuthority)
                .toList();
        List<AuthorityEntity> authorities = authorityRepository.findByAuthorityIn(authorityNames);
        if(authorities.size() != requestedAuthorities.size())
            throw new SpecificationBrokenException("There are some authorities than cannot be resolved: " + authorities);

        user.setAuthority(authorities);
    }
}
