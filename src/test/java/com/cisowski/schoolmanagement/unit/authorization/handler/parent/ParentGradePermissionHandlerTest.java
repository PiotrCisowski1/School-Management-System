package com.cisowski.schoolmanagement.unit.authorization.handler.parent;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.parent.ParentGradePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.service.GradeService;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
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
class ParentGradePermissionHandlerTest {

    @Mock
    private GradeService gradeService;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private ParentGradePermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnParent() {
        UserType result = handler.getSupportedUserType();

        assertEquals(UserType.PARENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnGrade() {
        ResourceType result = handler.getSupportedResourceType();

        assertEquals(ResourceType.GRADE, result);
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.CREATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenParentEntityIsNull_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenStudentIdProvidedAndIsChild_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        Integer studentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        parent.setChildren(List.of(child));
        child.setId(studentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenStudentIdProvidedAndIsNotChild_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        Integer studentId = 123;
        Integer differentStudentId = 456;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        parent.setChildren(List.of(child));
        child.setId(differentStudentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenGradeIdProvidedAndIsChildGradeOwner_ShouldReturnTrue() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        GradeEntity grade = Instancio.create(GradeEntity.class);
        Long gradeId = 789L;
        Integer childId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(gradeId);
        when(gradeService.fetchGrade(gradeId)).thenReturn(grade);

        parent.setChildren(List.of(child));
        child.setId(childId);
        grade.setStudent(child);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(gradeService).fetchGrade(gradeId);
    }

    @Test
    void canAccess_WhenGradeIdProvidedAndIsNotChildGradeOwner_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        StudentEntity child = Instancio.create(StudentEntity.class);
        StudentEntity otherChild = Instancio.create(StudentEntity.class);
        GradeEntity grade = Instancio.create(GradeEntity.class);
        Long gradeId = 789L;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(gradeId);
        when(gradeService.fetchGrade(gradeId)).thenReturn(grade);

        parent.setChildren(List.of(child));
        grade.setStudent(otherChild);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(gradeService).fetchGrade(gradeId);
    }

    @Test
    void canAccess_WhenNoStudentIdOrGradeIdProvided_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(gradeService, never()).fetchGrade(anyLong());
    }

    @Test
    void canAccess_WhenParentHasNoChildren_ShouldReturnFalse() {
        ParentEntity parent = Instancio.create(ParentEntity.class);
        Integer studentId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.PARENT_ENTITY)).thenReturn(parent);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        parent.setChildren(Collections.emptyList());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void requiresFiltering_ShouldReturnFalse() {
        boolean result = handler.requiresFiltering();

        assertFalse(result);
    }
}