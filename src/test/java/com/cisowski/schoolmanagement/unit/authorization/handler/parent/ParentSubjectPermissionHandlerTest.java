package com.cisowski.schoolmanagement.unit.authorization.handler.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent.ParentSubjectPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParentSubjectPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private ParentSubjectPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnParent() {
        UserType result = handler.getSupportedUserType();

        assertEquals(UserType.PARENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnSubject() {
        ResourceType result = handler.getSupportedResourceType();

        assertEquals(ResourceType.SUBJECT, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectIdMatchesChildsSubject_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        Integer subjectId = subject.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);
        yearbook.setMainCourseSubjects(List.of(subject));

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectIdDoesNotMatchAnyChildsSubject_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        SubjectEntity differentSubject = Instancio.create(SubjectEntity.class);
        Integer subjectId = differentSubject.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);
        yearbook.setMainCourseSubjects(List.of(subject));

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndParentEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectIdIsNull_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsWrite_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsDelete_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.DELETE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsCreate_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenMultipleChildrenAndOneHasSubject_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child1 = Instancio.create(StudentEntity.class);
        StudentEntity child2 = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook1 = Instancio.create(YearbookEntity.class);
        YearbookEntity yearbook2 = Instancio.create(YearbookEntity.class);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        Integer subjectId = subject.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        parent.setChildren(List.of(child1, child2));
        child1.setYearbook(yearbook1);
        child2.setYearbook(yearbook2);
        yearbook1.setMainCourseSubjects(Collections.emptyList());
        yearbook2.setMainCourseSubjects(List.of(subject));

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenMultipleChildrenAndNoneHaveSubject_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child1 = Instancio.create(StudentEntity.class);
        StudentEntity child2 = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook1 = Instancio.create(YearbookEntity.class);
        YearbookEntity yearbook2 = Instancio.create(YearbookEntity.class);
        SubjectEntity subject1 = Instancio.create(SubjectEntity.class);
        SubjectEntity subject2 = Instancio.create(SubjectEntity.class);
        SubjectEntity differentSubject = Instancio.create(SubjectEntity.class);
        Integer subjectId = differentSubject.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        parent.setChildren(List.of(child1, child2));
        child1.setYearbook(yearbook1);
        child2.setYearbook(yearbook2);
        yearbook1.setMainCourseSubjects(List.of(subject1));
        yearbook2.setMainCourseSubjects(List.of(subject2));

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenParentHasNoChildren_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        Integer subjectId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        parent.setChildren(Collections.emptyList());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenYearbookHasNoSubjects_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        Integer subjectId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        parent.setChildren(List.of(child));
        child.setYearbook(yearbook);
        yearbook.setMainCourseSubjects(Collections.emptyList());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void requiresFiltering_ShouldReturnFalse() {
        boolean result = handler.requiresFiltering();

        assertFalse(result);
    }
}