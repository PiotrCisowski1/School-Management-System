package com.cisowski.schoolmanagement.common.security.authorization;

import com.cisowski.schoolmanagement.common.security.authorization.handler.ResourcePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionHandlerKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.policy.ResourceAccessPolicy;
import com.cisowski.schoolmanagement.common.security.authorization.resolver.ResourceResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class AuthorizationRegistry {

    @Bean
    public Map<ResourceType, ResourceAccessPolicy<?>> policyMap(List<ResourceAccessPolicy<?>> policies){
        return policies.stream().collect(Collectors.toMap(
                ResourceAccessPolicy::getResourceType,
                Function.identity()
        ));
    }

//    @Bean
//    public Map<ResourceType, ResourceResolver> resolverMap(List<ResourceResolver> resolvers){
//        return resolvers.stream().collect(Collectors.toMap(
//                ResourceResolver::getResourceType,
//                Function.identity()
//        ));
//    }

    @Bean
    public Map<PermissionHandlerKey, ResourcePermissionHandler<?>> permissionHandlerMap(List<ResourcePermissionHandler<?>> handlers) {
        return handlers.stream()
                .collect(Collectors.toMap(
                        handler -> new PermissionHandlerKey(
                                handler.getSupportedUserType(),
                                handler.getSupportedResourceType()
                        ),
                        Function.identity()
                ));
    }
}
