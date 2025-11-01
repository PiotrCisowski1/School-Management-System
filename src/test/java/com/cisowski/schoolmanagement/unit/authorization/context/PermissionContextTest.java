package com.cisowski.schoolmanagement.unit.authorization.context;

import com.cisowski.schoolmanagement.common.security.authorization.context.PermissionContext;
import com.cisowski.schoolmanagement.common.security.authorization.model.PermissionContextAttributeKey;
import com.cisowski.schoolmanagement.common.security.authorization.model.UserType;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PermissionContextTest {

    @Test
    void isUser_WhenAuthorityMatchesUserType_ShouldReturnTrue() {
        UserEntity user = Instancio.create(UserEntity.class);
        PermissionContext context = new PermissionContext();
        context.setUser(user);

        Set<AuthorityEntity> authorities = new HashSet<>();
        AuthorityEntity teacherAuthority = new AuthorityEntity();
        teacherAuthority.setAuthority(UserType.TEACHER.toString());
        authorities.add(teacherAuthority);

        context.setAuthorities(authorities);

        boolean result = context.isUser(UserType.TEACHER);

        assertTrue(result);
    }

    @Test
    void isUser_WhenAuthorityDoesNotMatch_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        PermissionContext context = new PermissionContext();
        context.setUser(user);

        Set<AuthorityEntity> authorities = new HashSet<>();
        AuthorityEntity studentAuthority = new AuthorityEntity();
        studentAuthority.setAuthority(UserType.STUDENT.toString());
        authorities.add(studentAuthority);

        context.setAuthorities(authorities);

        boolean result = context.isUser(UserType.TEACHER);

        assertFalse(result);
    }

    @Test
    void isUser_WhenNoAuthorities_ShouldReturnFalse() {
        UserEntity user = Instancio.create(UserEntity.class);
        PermissionContext context = new PermissionContext();
        context.setUser(user);
        context.setAuthorities(new HashSet<>());

        boolean result = context.isUser(UserType.TEACHER);

        assertFalse(result);
    }

    @Test
    void putAttributeAndGetAttribute_ShouldStoreAndRetrieveValue() {
        PermissionContext context = new PermissionContext();
        PermissionContextAttributeKey key = PermissionContextAttributeKey.TEACHER_ENTITY;
        TeacherEntity expectedValue = Instancio.create(TeacherEntity.class);

        context.putAttribute(key, expectedValue);
        TeacherEntity actualValue = context.getAttribute(key);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    void putAttribute_WithIncorrectType_ShouldThrowIllegalArgumentException() {
        PermissionContext context = new PermissionContext();
        PermissionContextAttributeKey key = Instancio.create(PermissionContextAttributeKey.class);
        Integer incorrectValue = 123;

        IllegalArgumentException thrown = assertThrows(
                IllegalArgumentException.class,
                () -> context.putAttribute(key, incorrectValue)
        );

        assertTrue(thrown.getMessage().contains("Attribute type mismatch"));
    }

    @Test
    void getAttribute_WhenKeyNotExists_ShouldReturnNull() {
        PermissionContext context = new PermissionContext();
        PermissionContextAttributeKey key = Instancio.create(PermissionContextAttributeKey.class);

        String result = context.getAttribute(key);

        assertNull(result);
    }

    @Test
    void getPrimaryUserType_WhenTeacherAuthorityExists_ShouldReturnTeacher() {
        PermissionContext context = createContextWithAuthority(UserType.TEACHER);

        UserType result = context.getPrimaryUserType();

        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getPrimaryUserType_WhenStudentAuthorityExists_ShouldReturnStudent() {
        PermissionContext context = createContextWithAuthority(UserType.STUDENT);

        UserType result = context.getPrimaryUserType();

        assertEquals(UserType.STUDENT, result);
    }

    @Test
    void getPrimaryUserType_WhenParentAuthorityExists_ShouldReturnParent() {
        PermissionContext context = createContextWithAuthority(UserType.PARENT);

        UserType result = context.getPrimaryUserType();

        assertEquals(UserType.PARENT, result);
    }

    @Test
    void getPrimaryUserType_WhenMultipleAuthorities_ShouldReturnFirstMatching() {
        PermissionContext context = new PermissionContext();
        context.setUser(Instancio.create(UserEntity.class));

        Set<AuthorityEntity> authorities = new HashSet<>();
        authorities.add(createAuthority(UserType.STUDENT));
        authorities.add(createAuthority(UserType.TEACHER));
        context.setAuthorities(authorities);

        UserType result = context.getPrimaryUserType();

        assertEquals(UserType.TEACHER, result);
    }

    @Test
    void getPrimaryUserType_WhenNoMatchingAuthority_ShouldReturnNull() {
        PermissionContext context = new PermissionContext();
        context.setUser(Instancio.create(UserEntity.class));

        Set<AuthorityEntity> authorities = new HashSet<>();
        AuthorityEntity otherAuthority = new AuthorityEntity();
        otherAuthority.setAuthority("ADMIN");
        authorities.add(otherAuthority);
        context.setAuthorities(authorities);

        UserType result = context.getPrimaryUserType();

        assertNull(result);
    }

    @Test
    void getPrimaryUserType_WhenNoAuthorities_ShouldReturnNull() {
        PermissionContext context = new PermissionContext();
        context.setUser(Instancio.create(UserEntity.class));
        context.setAuthorities(new HashSet<>());

        UserType result = context.getPrimaryUserType();

        assertNull(result);
    }

    private PermissionContext createContextWithAuthority(UserType userType) {
        PermissionContext context = new PermissionContext();
        context.setUser(Instancio.create(UserEntity.class));

        Set<AuthorityEntity> authorities = new HashSet<>();
        authorities.add(createAuthority(userType));
        context.setAuthorities(authorities);

        return context;
    }

    private AuthorityEntity createAuthority(UserType userType) {
        AuthorityEntity authority = new AuthorityEntity();
        authority.setAuthority(userType.toString());
        return authority;
    }
}