package com.cisowski.schoolmanagement.common.security.authorization.enricher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.schedule.service.ScheduleVersionService;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.parent.service.ParentService;
import org.springframework.stereotype.Component;

@Component
public class ParentPermissionContextEnricher extends BasePermissionContextEnricher {

    private final ParentService parentService;

    public ParentPermissionContextEnricher(ScheduleVersionService scheduleVersionService, ParentService parentService) {
        super(scheduleVersionService);
        this.parentService = parentService;
    }

    @Override
    public void enrich(PermissionContext context, ResourceAccessContext accessContext) {
        Integer parentId = context.getUser().getId();
        ParentEntity parent = parentService.fetchParentEntity(parentId);
        context.putAttribute(PermissionContextAttributeKey.PARENT_ENTITY, parent);

        if (context.getResourceType().equals(ResourceType.SCHEDULE) ||
                context.getResourceType().equals(ResourceType.SCHEDULE_VERSION))
            this.addScheduleVersionToContext(context, accessContext);
    }

    @Override
    public boolean supports(UserEntity user) {
        return user.getAuthority().stream()
                .anyMatch(authority -> authority.getAuthority().equalsIgnoreCase(UserType.PARENT.name()));
    }
}