package com.cisowski.schoolmanagement.unit.authorization.handler.teacher;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.teacher.TeacherParentPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeacherParentPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private TeacherParentPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnTeacher() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnParent() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.PARENT, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherHasAccessToParent_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setParents(Collections.singletonList(parent));
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.singletonList(student));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(yearbook);
        Integer parentId = parent.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(parentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherDoesNotHaveAccessToParent_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setParents(Collections.singletonList(parent));
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.singletonList(student));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(yearbook);
        ParentEntity differentParent = Instancio.create(ParentEntity.class);
        Integer parentId = differentParent.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(parentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherEntityIsNull_ShouldReturnFalse() {
        Integer parentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndParentIdIsNull_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenLeadingYearbookIsNull_ShouldReturnFalse() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(null);
        Integer parentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(parentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenYearbookHasNoStudents_ShouldReturnFalse() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.emptyList());
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(yearbook);
        Integer parentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(parentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenStudentsHaveNoParents_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setParents(Collections.emptyList());
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setStudentsInYearbook(Collections.singletonList(student));
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        teacher.setLeadingYearbook(yearbook);
        Integer parentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.TEACHER_ENTITY)).thenReturn(teacher);
        when(resourceAccessContext.getAccessedMethodParameter("parentId")).thenReturn(parentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}