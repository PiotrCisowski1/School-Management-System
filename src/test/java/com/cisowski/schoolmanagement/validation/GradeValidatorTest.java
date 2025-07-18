package com.cisowski.schoolmanagement.validation;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.grade.validation.GradeValidator;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import org.instancio.Instancio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GradeValidatorTest {

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private GradeValidator gradeValidator;

    @Test
    void checkTeacherAssociatedWithSubject_shouldPassWhenTeacherHasSubject() {
        SubjectEntity subject = Instancio.create(SubjectEntity.class);
        TeacherEntity teacher = Instancio.of(TeacherEntity.class)
                .set(field(TeacherEntity::getTeachingSubjects), Set.of(subject))
                .create();

        assertDoesNotThrow(() ->
                gradeValidator.checkTeacherAssociatedWithSubject(teacher, subject)
        );
    }

    @Test
    void checkTeacherAssociatedWithSubject_shouldThrowWhenTeacherLacksSubject() {
        SubjectEntity requiredSubject = Instancio.create(SubjectEntity.class);
        SubjectEntity otherSubject = Instancio.create(SubjectEntity.class);

        TeacherEntity teacher = Instancio.of(TeacherEntity.class)
                .set(field(TeacherEntity::getTeachingSubjects), Set.of(otherSubject))
                .create();

        SpecificationBrokenException exception = assertThrows(SpecificationBrokenException.class,
                () -> gradeValidator.checkTeacherAssociatedWithSubject(teacher, requiredSubject)
        );

        assertTrue(exception.getMessage().contains(
                String.format("Teacher with ID: %s, has no right to give grade for Subject with ID: %s",
                        teacher.getId(), requiredSubject.getId())
        ));
    }

    @Test
    void isGradeOwner_shouldReturnTrueWhenGradeExistsForTeacher() {
        Integer teacherId = 1;
        Long gradeId = 100L;

        when(gradeRepository.existsByIdAndTeacherId(gradeId, teacherId)).thenReturn(true);

        assertTrue(gradeValidator.isGradeOwner(teacherId, gradeId));
        verify(gradeRepository).existsByIdAndTeacherId(gradeId, teacherId);
    }

    @Test
    void isGradeOwner_shouldReturnFalseWhenGradeNotFoundForTeacher() {
        Integer teacherId = 1;
        Long gradeId = 100L;

        when(gradeRepository.existsByIdAndTeacherId(gradeId, teacherId)).thenReturn(false);

        assertFalse(gradeValidator.isGradeOwner(teacherId, gradeId));
        verify(gradeRepository).existsByIdAndTeacherId(gradeId, teacherId);
    }
}
