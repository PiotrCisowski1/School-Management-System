package com.cisowski.schoolmanagement.common.security.authorization;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContextBuilder;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.enricher.PermissionContextEnricher;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.policy.ResourceAccessPolicy;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import io.jsonwebtoken.lang.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorizationService {

    private final PermissionContextBuilder permissionContextBuilder;
    private final Map<ResourceType, ResourceAccessPolicy<?>> policies;
    private final List<PermissionContextEnricher> enrichers;

    public <T> boolean canAccess(UserEntity user, ResourceAccessContext accessContext, ResourceType resourceType, ResourceActionType actionType){
        DbLogger.info(String.format(
                "Checking if User with ID: %s, can reach Resource %s with permission to %s",
                user.getId(),
                resourceType.name(),
                actionType.name()));
        PermissionContext context = permissionContextBuilder.build(user, resourceType, actionType);

        if(!Collections.isEmpty(enrichers))
            for (PermissionContextEnricher enricher : enrichers){
                if(enricher.supports(user))
                    enricher.enrich(context, accessContext);
        }

        ResourceAccessPolicy<T> policy = (ResourceAccessPolicy<T>) policies.get(resourceType);
        if(policy == null)
            throw new UnsupportedOperationException("No ResourceAccessPolicy found for resource type: " + resourceType);
        return policy.canAccess(context, accessContext);
    }
}
