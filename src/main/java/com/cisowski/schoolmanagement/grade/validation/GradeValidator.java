package com.cisowski.schoolmanagement.grade.validation;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class GradeValidator {

    private final GradeRepository gradeRepository;

    public void checkTeacherAssociatedWithSubject(TeacherEntity teacher, SubjectEntity subject) {
        teacher.getTeachingSubjects().stream()
                .filter(Objects::nonNull)
                .filter(subj -> subj.equals(subject))
                .findFirst()
                .orElseThrow(() -> new SpecificationBrokenException(String.format(
                        "Teacher with ID: %s, has no right to give grade for Subject with ID: %s",
                        teacher.getId(),
                        subject.getId()
                )));
    }

    public boolean isGradeOwner(Integer teacherId, Long gradeId){
        return gradeRepository.existsByIdAndTeacherId(gradeId, teacherId);
    }
}
