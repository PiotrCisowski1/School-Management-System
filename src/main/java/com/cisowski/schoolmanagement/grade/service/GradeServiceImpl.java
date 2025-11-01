package com.cisowski.schoolmanagement.grade.service;

import com.cisowski.schoolmanagement.common.exception.type.EntityNotFoundException;
import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.common.utility.DbLogger;
import com.cisowski.schoolmanagement.grade.mapper.GradeMapper;
import com.cisowski.schoolmanagement.grade.model.grade.*;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.grade.validation.GradeValidator;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.service.SubjectService;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.service.StudentService;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.service.TeacherService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GradeServiceImpl implements GradeService {

    private final GradeRepository gradeRepository;
    private final TeacherService teacherService;
    private final StudentService studentService;
    private final SubjectService subjectService;
    private final GradeTypeService gradeTypeService;
    private final GradeScaleService gradeScaleService;
    private final GradeMapper gradeMapper;
    private final GradeValidator gradeValidator;

    @Override
    @Transactional
    public GradeDetailedResponse addGrade(AddGradeRequest request) {
        DbLogger.info("Creating Grade for request: " + request.toString());
        GradeEntity grade = createGradeEntity(request);

        GradeEntity saved = gradeRepository.save(grade);
        DbLogger.info("Grade was successfully saved: " + saved.toString());

        return gradeMapper.toDetailedResponse(saved);
    }

    @Override
    @Transactional
    public GradeDetailedResponse patchGrade(PatchGradeRequest request, Long gradeId) {
        DbLogger.info(String.format("Updating Grade with ID %s for given request: %s", gradeId, request.toString()));
        GradeEntity grade = fetchGrade(gradeId);
        patchGradeEntity(grade, request);
        GradeEntity saved = gradeRepository.save(grade);
        DbLogger.info("Grade was updated successfully: " + saved.toString());
        return gradeMapper.toDetailedResponse(saved);
    }

    @Override
    @Transactional
    public void deleteGrade(Long gradeId) {
        DbLogger.info("Deleting Grade with ID: " + gradeId);
        GradeEntity grade = fetchGrade(gradeId);
        gradeRepository.delete(grade);
        DbLogger.info(String.format("Grade with ID: %s, was removed successfully", gradeId));
    }

    @Override
    public GradeDetailedResponse getGradeById(Long gradeId) {
        DbLogger.info("Searching for Grade with ID: " + gradeId);
        GradeEntity grade = fetchGrade(gradeId);
        return gradeMapper.toDetailedResponse(grade);
    }

    @Override
    public List<GradeSummaryResponse> getGradesByStudentId(Integer studentId) {
        DbLogger.info("Searching for Grades for Student with ID: " + studentId);
        StudentEntity student = studentService.fetchStudent(studentId);
        List<GradeEntity> grades = gradeRepository.findByStudent(student);
        DbLogger.info(String.format("Found %s Grades for Student with ID: %s", grades.size(), studentId));
        return gradeMapper.toSummaryResponseList(grades);
    }

    @Override
    public List<GradeSummaryResponse> getGradesByStudentIdAndSubject(Integer studentId, Integer subjectId) {
        DbLogger.info(String.format("Searching for Grades for Student with ID: %s and Subject with ID: %s",studentId, subjectId));
        StudentEntity student = studentService.fetchStudent(studentId);
        SubjectEntity subject = subjectService.fetchSubject(subjectId);
        List<GradeEntity> grades = gradeRepository.findByStudentAndSubject(student, subject);
        DbLogger.info(String.format("Found %s Grades for Student with ID: %s and Subject with ID: %s", grades.size(), studentId, subjectId));
        return gradeMapper.toSummaryResponseList(grades);
    }

    @Override
    public List<GradeSummaryResponse> getGradesBySubjectId(Integer subjectId) {
        DbLogger.info("Searching for Grades for Subject with ID: %s" + subjectId);
        SubjectEntity subject = subjectService.fetchSubject(subjectId);
        List<GradeEntity> grades = gradeRepository.findBySubject(subject);
        DbLogger.info("Found %s Grades for Subject with ID: %s" + subjectId);
        return gradeMapper.toSummaryResponseList(grades);
    }

    @Override
    public List<GradeSummaryResponse> getGradesByGradeType(Long gradeTypeId) {
        DbLogger.info("Searching for Grades for GradeType with ID: %s" + gradeTypeId);
        GradeTypeEntity gradeType = gradeTypeService.fetchGradeType(gradeTypeId);
        List<GradeEntity> grades = gradeRepository.findByGradeType(gradeType);
        DbLogger.info("Found %s Grades for GradeType with ID: %s" + gradeType);
        return gradeMapper.toSummaryResponseList(grades);
    }

    @Override
    public GradeEntity fetchGrade(Long gradeId) {
        DbLogger.info("Searching for Grade with ID " + gradeId);
        Optional<GradeEntity> grade = gradeRepository.findById(gradeId);
        if(grade.isEmpty())
            throw new EntityNotFoundException(GradeEntity.class, "ID", gradeId.toString());
        return grade.get();
    }

    private void patchGradeEntity(GradeEntity grade, PatchGradeRequest request) {
        SubjectEntity subject = null;
        if (request.getStudentId() != null)
            grade.setStudent(studentService.fetchStudent(request.getStudentId()));
        if (request.getSubjectId() != null) {
            subject = subjectService.fetchSubject(request.getSubjectId());
            grade.setSubject(subject);
        }
        if (request.getGradeTypeId() != null)
            grade.setGradeType(gradeTypeService.fetchGradeType(request.getGradeTypeId()));
        if (request.getGradeValueId() != null)
            grade.setGradeValue(gradeScaleService.fetchGradeValue(request.getGradeValueId()));
        if (StringUtils.isNotEmpty(request.getComments()))
            grade.setComments(request.getComments());
        if(subject != null)
            gradeValidator.checkTeacherAssociatedWithSubject(grade.getTeacher(), subject);
    }

    private GradeEntity createGradeEntity(AddGradeRequest request) {
        GradeEntity grade = gradeMapper.toEntity(request);

        StudentEntity student = studentService.fetchStudent(request.getStudentId());
        TeacherEntity teacher = teacherService.fetchTeacher(request.getTeacherId());
        SubjectEntity subject = subjectService.fetchSubject(request.getSubjectId());
        gradeValidator.checkTeacherAssociatedWithSubject(teacher, subject);
        GradeTypeEntity gradeType = gradeTypeService.fetchGradeType(request.getGradeTypeId());
        GradeValueEntity gradeValue = gradeScaleService.fetchGradeValue(request.getGradeValueId());

        grade.setStudent(student);
        teacher = (TeacherEntity) Hibernate.unproxy(teacher);
        grade.setTeacher(teacher);
        grade.setSubject(subject);
        grade.setGradeType(gradeType);
        grade.setGradeValue(gradeValue);

        return grade;
    }
}
