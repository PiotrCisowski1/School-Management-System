package com.cisowski.schoolmanagement.unit.authorization.handler.student;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.handler.impl.student.StudentTeacherPermissionHandler;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceActionType;
import com.cisowski.schoolmanagement.common.security.authorization.model.ResourceType;
import com.cisowski.schoolmanagement.common.security.authorization.context.ResourceAccessContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
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
class StudentTeacherPermissionHandlerTest {

    @Mock
    private PermissionContext permissionContext;

    @Mock
    private ResourceAccessContext resourceAccessContext;

    @InjectMocks
    private StudentTeacherPermissionHandler handler;

    @Test
    void getSupportedUserType_ShouldReturnStudent() {
        UserType result = handler.getSupportedUserType();
        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void getSupportedResourceType_ShouldReturnTeacher() {
        ResourceType result = handler.getSupportedResourceType();
        assertEquals(ResourceType.TEACHER, result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherIsHeadTeacher_ShouldReturnTrue() {
        TeacherEntity headTeacher = Instancio.create(TeacherEntity.class);
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setHeadTeacher(headTeacher);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(headTeacher.getId());

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherIsSubjectTeacher_ShouldReturnTrue() {
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity subjectTeacher = Instancio.create(TeacherEntity.class);
        subject.setTeachers(Collections.singletonList(subjectTeacher));
        yearbook.setMainCourseSubjects(Collections.singletonList(subject));
        Integer teacherId = subjectTeacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertTrue(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherIsNeitherHeadNorSubjectTeacher_ShouldReturnFalse() {
        TeacherEntity headTeacher = Instancio.create(TeacherEntity.class);
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity subjectTeacher = Instancio.create(TeacherEntity.class);
        subject.setTeachers(Collections.singletonList(subjectTeacher));
        YearbookEntity yearbook = Instancio.create(YearbookEntity.class);
        yearbook.setHeadTeacher(headTeacher);
        yearbook.setMainCourseSubjects(Collections.singletonList(subject));
        StudentEntity student = Instancio.create(StudentEntity.class);
        student.setYearbook(yearbook);
        TeacherEntity differentTeacher = Instancio.create(TeacherEntity.class);
        Integer teacherId = differentTeacher.getId();

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(teacherId);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndStudentEntityIsNull_ShouldReturnFalse() {
        Integer teacherId = 123;

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(null);

        boolean result = handler.canAccess(permissionContext, resourceAccessContext);

        assertFalse(result);
    }

    @Test
    void canAccess_WhenActionIsReadAndTeacherIdIsNull_ShouldReturnFalse() {
        StudentEntity student = Instancio.create(StudentEntity.class);

        when(permissionContext.getAction()).thenReturn(ResourceActionType.READ);
        when(permissionContext.getAttribute(PermissionContextAttributeKey.STUDENT_ENTITY)).thenReturn(student);
        when(resourceAccessContext.getAccessedMethodParameter("teacherId")).thenReturn(null);

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
}