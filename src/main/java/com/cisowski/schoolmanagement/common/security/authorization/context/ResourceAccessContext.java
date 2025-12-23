package com.cisowski.schoolmanagement.common.security.authorization.context;

import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;


public class ResourceAccessContext {
    private Map<String, Object> params;
    @Getter
    private ResourceType resourceType;
    @Getter
    private ResourceActionType actionType;

    public ResourceAccessContext(ResourceType resourceType, ResourceActionType actionType) {
        this.actionType = actionType;
        this.params = new HashMap<>();
        this.resourceType = resourceType;
    }

    public void put(String key, Object value){
        params.put(key, value);
    }

    public <T> T getAccessedMethodParameter(String exactParameterName){
        return (T) params.get(exactParameterName);
    }
}
