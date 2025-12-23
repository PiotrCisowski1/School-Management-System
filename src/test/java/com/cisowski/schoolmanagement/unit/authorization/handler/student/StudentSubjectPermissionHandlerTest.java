package com.cisowski.schoolmanagement.unit.authorization.handler.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student.StudentSubjectPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentSubjectPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private StudentSubjectPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnStudent() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnSubject() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.SUBJECT, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectIdMatches_ShouldReturnTrue() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setMainCourseSubjects(Collections.singletonList(subject));
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        Integer subjectId = subject.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectIdDoesNotMatch_ShouldReturnFalse() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setMainCourseSubjects(Collections.singletonList(subject));
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        SubjectEntity differentSubject = Instancio.create(SubjectEntity.class);
        Integer subjectId = differentSubject.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndStudentEntityIsNull_ShouldReturnFalse() {
        Integer subjectId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndSubjectIdIsNull_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
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
    void canAccess_WhenYearbookHasNoSubjects_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setMainCourseSubjects(Collections.emptyList());
        student.setYearbook(yearbook);
        Integer subjectId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}