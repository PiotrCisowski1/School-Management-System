package com.cisowski.schoolmanagement.unit.authorization.handler.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student.StudentGradePermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.service.GradeService;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentGradePermissionHandlerTest {

    @Mock
    private GradeService gradeService;

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private StudentGradePermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnStudent() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnGrade() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.GRADE, result);
    }

    @Test
    void canAccess_WhenActionIsNotRead_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.UPDATE);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenGradeIdProvidedAndIsStudentGradeOwner_ShouldReturnTrue() {
        StudentEntity user = Instancio.create(StudentEntity.class);
        GradeEntity grade = mock(GradeEntity.class);
        Long gradeId = 123L;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(gradeId);
        when(gradeService.fetchGrade(gradeId)).thenReturn(grade);
        when(grade.getStudent()).thenReturn(user);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
        verify(gradeService).fetchGrade(gradeId);
    }

    @Test
    void canAccess_WhenGradeIdProvidedAndIsNotStudentGradeOwner_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        StudentEntity differentUser = Instancio.create(StudentEntity.class);
        GradeEntity grade = mock(GradeEntity.class);
        Long gradeId = 123L;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(gradeId);
        when(gradeService.fetchGrade(gradeId)).thenReturn(grade);
        when(grade.getStudent()).thenReturn(differentUser);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
        verify(gradeService).fetchGrade(gradeId);
    }

    @Test
    void canAccess_WhenSubjectIdAndStudentIdProvidedAndUserMatches_ShouldReturnTrue() {
        UserEntity user = Instancio.create(UserEntity.class);
        StudentEntity student = mock(StudentEntity.class);
        YearbookEntity yearbook = mock(YearbookEntity.class);
        SubjectEntity subject = mock(SubjectEntity.class);
        Integer subjectId = 456;
        Integer studentId = user.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);
        when(student.getYearbook()).thenReturn(yearbook);
        when(yearbook.getMainCourseSubjects()).thenReturn(List.of(subject));
        when(subject.getId()).thenReturn(subjectId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenOnlyStudentIdProvidedAndUserMatches_ShouldReturnTrue() {
        UserEntity user = Instancio.create(UserEntity.class);
        Integer studentId = user.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenOnlyStudentIdProvidedAndUserDoesNotMatch_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        Integer differentStudentId = user.getId() + 1;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(differentStudentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenStudentEntityIsNullForSubjectCheck_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        Integer subjectId = 456;
        Integer studentId = user.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenYearbookHasNoMatchingSubject_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        StudentEntity student = mock(StudentEntity.class);
        YearbookEntity yearbook = mock(YearbookEntity.class);
        SubjectEntity subject = mock(SubjectEntity.class);
        Integer subjectId = 456;
        Integer differentSubjectId = 789;
        Integer studentId = user.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getUser()).thenReturn(user);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(subjectId);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(studentId);
        when(student.getYearbook()).thenReturn(yearbook);
        when(yearbook.getMainCourseSubjects()).thenReturn(List.of(subject));
        when(subject.getId()).thenReturn(differentSubjectId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenNoParametersProvided_ShouldReturnFalse() {
        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(resourceAccessContext.getAccessedMethodParameter("gradeId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("subjectId")).thenReturn(null);
        when(resourceAccessContext.getAccessedMethodParameter("studentId")).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }
}