package com.cisowski.schoolmanagement.integration.helper;

import com.cisowski.schoolmanagement.appConfig.model.AppConfigEntity;
import com.cisowski.schoolmanagement.appConfig.model.AppConfigValueType;
import com.cisowski.schoolmanagement.appConfig.repository.AppConfigRepository;
import com.cisowski.schoolmanagement.classroom.model.*;
import com.cisowski.schoolmanagement.classroom.repository.ClassroomRepository;
import com.cisowski.schoolmanagement.classroom.repository.EquipmentRepository;
import com.cisowski.schoolmanagement.grade.model.grade.AddGradeRequest;
import com.cisowski.schoolmanagement.grade.model.grade.GradeEntity;
import com.cisowski.schoolmanagement.grade.model.grade.PatchGradeRequest;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeScaleEntity;
import com.cisowski.schoolmanagement.grade.model.gradeScale.GradeValueEntity;
import com.cisowski.schoolmanagement.grade.model.gradeType.GradeTypeEntity;
import com.cisowski.schoolmanagement.grade.repository.GradeRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeScaleRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeTypeRepository;
import com.cisowski.schoolmanagement.grade.repository.GradeValueRepository;
import com.cisowski.schoolmanagement.timetable.schedule.model.*;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangeLogEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.AddScheduleVersionRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.PatchScheduleVersionRequest;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleChangelogRepository;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleRepository;
import com.cisowski.schoolmanagement.timetable.schedule.repository.ScheduleVersionRepository;
import com.cisowski.schoolmanagement.subject.model.AddSubjectRequest;
import com.cisowski.schoolmanagement.subject.model.SubjectEntity;
import com.cisowski.schoolmanagement.subject.model.SubjectTypeEntity;
import com.cisowski.schoolmanagement.subject.repository.SubjectRepository;
import com.cisowski.schoolmanagement.subject.repository.SubjectTypeRepository;
import com.cisowski.schoolmanagement.users.common.model.AddressEntity;
import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.common.repository.UserDetailsRepository;
import com.cisowski.schoolmanagement.users.parent.model.ParentCreateRequest;
import com.cisowski.schoolmanagement.users.parent.model.ParentEntity;
import com.cisowski.schoolmanagement.users.parent.model.ParentPatchRequest;
import com.cisowski.schoolmanagement.users.parent.repository.ParentRepository;
import com.cisowski.schoolmanagement.users.student.model.StudentCreateRequest;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentPatchRequest;
import com.cisowski.schoolmanagement.users.student.repository.StudentRepository;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherCreateRequest;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherPatchRequest;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityEntity;
import com.cisowski.schoolmanagement.users.teacher.model.availability.TeacherAvailabilityRequest;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherAvailabilityRepository;
import com.cisowski.schoolmanagement.users.teacher.repository.TeacherRepository;
import com.cisowski.schoolmanagement.yearbook.model.AddYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.PatchYearbookRequest;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import com.cisowski.schoolmanagement.yearbook.repository.YearbookRepository;
import lombok.RequiredArgsConstructor;
import org.instancio.Instancio;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.instancio.Select.field;

@Component
@RequiredArgsConstructor
public class TestDataHelper {

    private final UserDetailsRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final DatabaseHelper dbHelper;
    private final EquipmentRepository equipmentRepository;
    private final ClassroomRepository classroomRepository;
    private final ScheduleRepository scheduleRepository;
    private final ScheduleVersionRepository scheduleVersionRepository;
    private final YearbookRepository yearbookRepository;
    private final SubjectRepository subjectRepository;
    private final SubjectTypeRepository subjectTypeRepository;
    private final StudentRepository studentRepository;
    private final GradeTypeRepository gradeTypeRepository;
    private final GradeScaleRepository gradeScaleRepository;
    private final GradeValueRepository gradeValueRepository;
    private final ParentRepository parentRepository;
    private final GradeRepository gradeRepository;
    private final TeacherAvailabilityRepository teacherAvailabilityRepository;
    private final ScheduleChangelogRepository scheduleChangelogRepository;
    private final AppConfigRepository configRepository;

    public UserEntity createRandomAdminUser() {
        AuthorityEntity authority = dbHelper.fetchAuthorityByName("ADMINISTRATOR").orElse(null);
        AddressEntity address = createRandomAddressEntity();
        UserEntity user = Instancio.of(UserEntity.class)
                .set(field(UserEntity::getAuthority), Collections.singletonList(authority))
                .set(field(UserEntity::getAddress), address)
                .create();
        return userRepository.save(user);
    }

    public TeacherEntity createTeacher(List<SubjectEntity> teachingSubjects) {
        AuthorityEntity authority = dbHelper.fetchAuthorityByName("TEACHER").orElse(null);
        TeacherEntity teacher = Instancio.of(TeacherEntity.class)
                .set(field(TeacherEntity::getAuthority), Collections.singletonList(authority))
                .set(field(TeacherEntity::getTeachingSubjects), teachingSubjects)
                .set(field(TeacherEntity::getAvailability), null)
                .set(field(TeacherEntity::getLeadingYearbook), null)
                .set(field(TeacherEntity::getAddress), createRandomAddressEntity())
                .set(field(TeacherEntity::getIsEnabled), true)
                .create();
        return teacherRepository.save(teacher);
    }

    private AddressEntity createRandomAddressEntity() {
        return Instancio.of(AddressEntity.class)
                .set(field(AddressEntity::getId), null)
                .create();
    }

    public Equipment createRandomEq() {
        Equipment eq = new Equipment();
        eq.setName("test");
        return equipmentRepository.save(eq);
    }

    public ClassroomEntity createClassroom() {
        ClassroomEntity classroom = Instancio.of(ClassroomEntity.class)
                .set(field(ClassroomEntity::getClassroomEquipments), null)
                .set(field(ClassroomEntity::getId), null)
                .create();
        return classroomRepository.save(classroom);
    }

    public ClassroomEntity createClassroom(ScheduleVersionEntity scheduleVersion, TeacherEntity teacher, SubjectEntity subject) {
        ClassroomEntity classroom = createClassroom();
        ScheduleEntity schedule = createScheduleEntity(scheduleVersion, teacher, subject, classroom);
        scheduleRepository.save(schedule);
        return classroom;
    }

    public ScheduleVersionEntity createScheduleVersion(YearbookEntity yearbook) {
        if(yearbook == null)
            yearbook = createYearbook(Collections.emptyList(), null);
        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getId), null)
                .set(field(ScheduleVersionEntity::getYearbook), yearbook)
                .set(field(ScheduleVersionEntity::getSchedules), null)
                .set(field(ScheduleVersionEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();
        return scheduleVersionRepository.save(scheduleVersion);
    }

    public YearbookEntity createYearbook(List<SubjectEntity> subjects, TeacherEntity teacher) {
        YearbookEntity yearbook = Instancio.of(YearbookEntity.class)
                .set(field(YearbookEntity::getHeadTeacher), teacher)
                .set(field(YearbookEntity::getMainCourseSubjects), subjects)
                .set(field(YearbookEntity::getStudentsInYearbook), null)
                .set(field(YearbookEntity::getId), null)
                .create();

        YearbookEntity savedYearbook = yearbookRepository.save(yearbook);

        if (teacher != null) {
            teacher.setLeadingYearbook(savedYearbook);
            teacherRepository.save(teacher);
        }

        return savedYearbook;
    }

    public SubjectEntity createSubject() {
        SubjectTypeEntity subjectType = createSubjectType();
        SubjectEntity subject = Instancio.of(SubjectEntity.class)
                .set(field(SubjectEntity::getId), null)
                .set(field(SubjectEntity::getTeachers), null)
                .set(field(SubjectEntity::getSubjectType), subjectType)
                .set(field(SubjectEntity::getYearbooksTakingSubject), new ArrayList<>())
                .create();
        return subjectRepository.save(subject);
    }

    public SubjectTypeEntity createSubjectType() {
        SubjectTypeEntity subjectType = Instancio.of(SubjectTypeEntity.class)
                .set(field(SubjectTypeEntity::getId), null)
                .create();
        return subjectTypeRepository.save(subjectType);
    }

    public StudentEntity createStudent(YearbookEntity yearbook, List<ParentEntity> parents) {
        if(yearbook == null)
            yearbook = createYearbook(Collections.emptyList(), null);
        AuthorityEntity authority = dbHelper.fetchAuthorityByName("STUDENT").orElse(null);
        StudentEntity student = Instancio.of(StudentEntity.class)
                .set(field(StudentEntity::getId), null)
                .set(field(StudentEntity::getParents), parents)
                .set(field(StudentEntity::getAuthority), Collections.singletonList(authority))
                .set(field(StudentEntity::getAddress), createRandomAddressEntity())
                .set(field(StudentEntity::getYearbook), yearbook)
                .create();
        return studentRepository.save(student);
    }

    public GradeTypeEntity createGradeType() {
        GradeTypeEntity gradeType = Instancio.of(GradeTypeEntity.class)
                .set(field(GradeTypeEntity::getId), null)
                .create();
        return gradeTypeRepository.save(gradeType);
    }

    public List<GradeTypeEntity> createGradeTypes() {
        List<GradeTypeEntity> gradeTypes = Instancio.ofList(GradeTypeEntity.class)
                .set(field(GradeTypeEntity::getId), null)
                .create();
        return gradeTypeRepository.saveAll(gradeTypes);
    }

    public GradeScaleEntity createGradeScale(boolean isActiveScale) {
        GradeScaleEntity scaleEntity = Instancio.of(GradeScaleEntity.class)
                .set(field(GradeScaleEntity::getId), null)
                .set(field(GradeScaleEntity::getIsActive), isActiveScale)
                .set(field(GradeScaleEntity::getGradeValues), Collections.emptyList())
                .create();
        GradeScaleEntity saved = gradeScaleRepository.save(scaleEntity);
        GradeValueEntity gradeValue = createGradeValue(saved);
        return gradeScaleRepository.findById(saved.getId()).get();
    }

    public GradeValueEntity createGradeValue(GradeScaleEntity gradeScale) {
        if(gradeScale == null)
            gradeScale = createGradeScale(true);
        GradeValueEntity gradeValue = Instancio.of(GradeValueEntity.class)
                .set(field(GradeValueEntity::getId), null)
                .set(field(GradeValueEntity::getGradeScale), gradeScale)
                .create();
        return gradeValueRepository.save(gradeValue);
    }

    public ParentEntity createRandomParent() {
        AuthorityEntity authority = dbHelper.fetchAuthorityByName("PARENT").orElse(null);
        ParentEntity parent = Instancio.of(ParentEntity.class)
                .set(field(ParentEntity::getId), null)
                .set(field(ParentEntity::getChildren), Collections.emptyList())
                .set(field(StudentEntity::getAuthority), Collections.singletonList(authority))
                .set(field(StudentEntity::getAddress), createRandomAddressEntity())
                .create();
        return parentRepository.save(parent);
    }

    public AddGradeRequest createAddGradeRequest(TeacherEntity teacher, SubjectEntity subject, StudentEntity student, GradeTypeEntity gradeType) {
        if(subject == null)
            subject = createSubject();
        if(teacher == null)
            teacher = createTeacher(Collections.singletonList(subject));
        if(student == null)
            student = createStudent(null, null);
        if(gradeType == null)
            gradeType = createGradeType();
        GradeScaleEntity gradeScale = createGradeScale(true);
        GradeValueEntity gradeValue = createGradeValue(gradeScale);
        return Instancio.of(AddGradeRequest.class)
                .set(field(AddGradeRequest::getTeacherId), teacher.getId())
                .set(field(AddGradeRequest::getSubjectId), subject.getId())
                .set(field(AddGradeRequest::getStudentId), student.getId())
                .set(field(AddGradeRequest::getGradeTypeId), gradeType.getId())
                .set(field(AddGradeRequest::getGradeValueId), gradeValue.getId())
                .create();
    }

    public GradeEntity createGradeEntity(TeacherEntity teacher, SubjectEntity subject, boolean isGivenTeacherGradeOwner) {
        if(subject == null)
            subject = createSubject();
        YearbookEntity yearbook = createYearbook(Collections.singletonList(subject), null);
        StudentEntity student = createStudent(yearbook, null);
        GradeTypeEntity gradeType = createGradeType();
        GradeScaleEntity gradeScale = createGradeScale(true);
        GradeValueEntity gradeValue = createGradeValue(gradeScale);
        GradeEntity grade = Instancio.of(GradeEntity.class)
                .set(field(GradeEntity::getId), null)
                .set(field(GradeEntity::getStudent), student)
                .set(field(GradeEntity::getTeacher), isGivenTeacherGradeOwner ? teacher : createTeacher(Collections.singletonList(subject)))
                .set(field(GradeEntity::getSubject), subject)
                .set(field(GradeEntity::getGradeType), gradeType)
                .set(field(GradeEntity::getGradeValue), gradeValue)
                .create();
        return gradeRepository.save(grade);
    }

    public GradeEntity createGradeEntity(StudentEntity student) {
        if(student == null)
            student = createStudent(createYearbook(Collections.emptyList(), createTeacher(Collections.emptyList())), null);
        SubjectEntity subject = createSubject();
        TeacherEntity teacher = createTeacher(Collections.singletonList(subject));
        GradeTypeEntity gradeType = createGradeType();
        GradeScaleEntity gradeScale = createGradeScale(true);
        GradeValueEntity gradeValue = createGradeValue(gradeScale);
        GradeEntity grade = Instancio.of(GradeEntity.class)
                .set(field(GradeEntity::getId), null)
                .set(field(GradeEntity::getStudent), student)
                .set(field(GradeEntity::getTeacher), teacher)
                .set(field(GradeEntity::getSubject), subject)
                .set(field(GradeEntity::getGradeType), gradeType)
                .set(field(GradeEntity::getGradeValue), gradeValue)
                .create();
        return gradeRepository.save(grade);
    }

    public GradeEntity createGradeEntity(StudentEntity student, SubjectEntity subject, TeacherEntity teacher) {
        if(student == null)
            student = createStudent(createYearbook(Collections.emptyList(), createTeacher(Collections.emptyList())), null);
        if(subject == null)
            subject = createSubject();
        if(teacher == null)
            teacher = createTeacher(Collections.singletonList(subject));
        GradeTypeEntity gradeType = createGradeType();
        GradeScaleEntity gradeScale = createGradeScale(true);
        GradeValueEntity gradeValue = createGradeValue(gradeScale);
        GradeEntity grade = Instancio.of(GradeEntity.class)
                .set(field(GradeEntity::getId), null)
                .set(field(GradeEntity::getStudent), student)
                .set(field(GradeEntity::getTeacher), teacher)
                .set(field(GradeEntity::getSubject), subject)
                .set(field(GradeEntity::getGradeType), gradeType)
                .set(field(GradeEntity::getGradeValue), gradeValue)
                .create();
        return gradeRepository.save(grade);
    }

    public GradeEntity createGradeEntity(GradeTypeEntity gradeType) {
        StudentEntity student = createStudent(createYearbook(Collections.emptyList(), createTeacher(Collections.emptyList())), null);
        SubjectEntity subject = createSubject();
        TeacherEntity teacher = createTeacher(Collections.singletonList(subject));
        if(gradeType == null) gradeType = createGradeType();
        GradeScaleEntity gradeScale = createGradeScale(true);
        GradeValueEntity gradeValue = createGradeValue(gradeScale);
        GradeEntity grade = Instancio.of(GradeEntity.class)
                .set(field(GradeEntity::getId), null)
                .set(field(GradeEntity::getStudent), student)
                .set(field(GradeEntity::getTeacher), teacher)
                .set(field(GradeEntity::getSubject), subject)
                .set(field(GradeEntity::getGradeType), gradeType)
                .set(field(GradeEntity::getGradeValue), gradeValue)
                .create();
        return gradeRepository.save(grade);
    }

    public AddScheduleRequest createAddScheduleRequest(SubjectEntity subject, TeacherEntity teacher, ClassroomEntity classroom) {
        if(subject == null)
            subject = createSubject();
        if(teacher == null)
            teacher = createTeacher(null);
        if(classroom == null)
            classroom = createClassroom();

        LocalTime startTime = Instancio.gen().temporal().localTime()
                .range(LocalTime.of(1, 0), LocalTime.of(22, 0))
                .get();
        LocalTime endTime = startTime.plusHours(1);
        LocalDate effectiveDate = LocalDate.now().plusDays(1);
        LocalDate expirationDate = LocalDate.now().plusDays(2);

        return Instancio.of(AddScheduleRequest.class)
                .set(field(AddScheduleRequest::getSubjectId), subject.getId())
                .set(field(AddScheduleRequest::getTeacherId), teacher.getId())
                .set(field(AddScheduleRequest::getClassroomId), classroom.getId())
                .set(field(AddScheduleRequest::getStartTime), startTime)
                .set(field(AddScheduleRequest::getEndTime), endTime)
                .set(field(AddScheduleRequest::getEffectiveDate), effectiveDate)
                .set(field(AddScheduleRequest::getExpirationDate), expirationDate)
                .generate(field(AddScheduleRequest::getDayOfWeek), gen -> gen.ints().range(1,7))
                .generate(field(AddScheduleRequest::getRecurrenceType), gen -> gen.oneOf(ScheduleRecurrenceType.getProperRecurrenceTypes()))
                .create();
    }

    public ScheduleEntity createScheduleEntity(ScheduleVersionEntity scheduleVersion, TeacherEntity teacher, SubjectEntity subject) {
        if(teacher == null)
            teacher = createTeacher(Collections.emptyList());
        if(scheduleVersion == null)
            scheduleVersion = createScheduleVersion(null);
        if(subject == null)
            subject = createSubject();

        LocalDate effectiveDate = LocalDate.now().minusDays(1);
        LocalDate expirationDate = LocalDate.now().plusDays(1);

        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getTeacher), teacher)
                .set(field(ScheduleEntity::getScheduleVersion), scheduleVersion)
                .set(field(ScheduleEntity::getSubject), subject)
                .set(field(ScheduleEntity::getId), null)
                .set(field(ScheduleEntity::getClassroom), createClassroom())
                .generate(field(ScheduleEntity::getStartTime), gen -> gen.temporal().localTime().past()
                        .as(localTime -> localTime.withNano(0)))
                .generate(field(ScheduleEntity::getEndTime), gen -> gen.temporal().localTime().future()
                        .as(localTime -> localTime.withNano(0)))
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .set(field(ScheduleEntity::getEffectiveDate), effectiveDate)
                .set(field(ScheduleEntity::getExpirationDate), expirationDate)
                .generate(field(ScheduleEntity::getRecurrenceType), gen -> gen.oneOf(ScheduleRecurrenceType.getProperRecurrenceTypes()))
                .create();

        return scheduleRepository.save(schedule);
    }

    public ScheduleEntity createScheduleEntity(ScheduleVersionEntity scheduleVersion, TeacherEntity teacher, SubjectEntity subject, ClassroomEntity classroom) {
        if(teacher == null)
            teacher = createTeacher(Collections.emptyList());
        if(scheduleVersion == null)
            scheduleVersion = createScheduleVersion(null);
        if(subject == null)
            subject = createSubject();
        if(classroom == null)
            classroom = createClassroom();

        LocalDate effectiveDate = LocalDate.now().minusDays(1);
        LocalDate expirationDate = LocalDate.now().plusDays(1);

        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getTeacher), teacher)
                .set(field(ScheduleEntity::getScheduleVersion), scheduleVersion)
                .set(field(ScheduleEntity::getSubject), subject)
                .set(field(ScheduleEntity::getId), null)
                .set(field(ScheduleEntity::getClassroom), classroom)
                .generate(field(ScheduleEntity::getStartTime), gen -> gen.temporal().localTime().past()
                        .as(localTime -> localTime.withNano(0)))
                .generate(field(ScheduleEntity::getEndTime), gen -> gen.temporal().localTime().future()
                        .as(localTime -> localTime.withNano(0)))
                .set(field(ScheduleEntity::getEffectiveDate), effectiveDate)
                .set(field(ScheduleEntity::getExpirationDate), expirationDate)
                .generate(field(ScheduleEntity::getRecurrenceType), gen -> gen.oneOf(ScheduleRecurrenceType.getProperRecurrenceTypes()))
                .create();

        return scheduleRepository.save(schedule);
    }

    public AddSubjectRequest createAddSubjectRequest(SubjectTypeEntity subjectType) {
        return Instancio.of(AddSubjectRequest.class)
                .set(field(AddSubjectRequest::getSubjectTypeId), subjectType.getId())
                .generate(field(AddSubjectRequest::getCode), gen -> gen.string().maxLength(6))
                .create();
    }

    public AddYearbookRequest createAddYearbookRequest(TeacherEntity teacher, List<SubjectEntity> subjects) {
        if(teacher == null)
            teacher = createTeacher(null);
        if(CollectionUtils.isEmpty(subjects)) {
            subjects = new ArrayList<>();
            subjects.add(createSubject());
        }

        List<Integer> subjectIds = subjects.stream()
                .map(SubjectEntity::getId)
                .collect(Collectors.toList());

        return Instancio.of(AddYearbookRequest.class)
                .set(field(AddYearbookRequest::getHeadTeacherId), teacher.getId())
                .set(field(AddYearbookRequest::getMainCourseSubjectsIds), subjectIds)
                .create();
    }

    public ParentCreateRequest createParentCreateRequest(List<Integer> childrenIds) {
        AuthorityEntity authority = dbHelper.fetchAuthorityByName("PARENT").orElse(null);
        return Instancio.of(ParentCreateRequest.class)
                .set(field(ParentCreateRequest::getAuthority), Collections.singletonList(authority))
                .generate(field(ParentCreateRequest::getEmail), gen -> gen.net().email())
                .generate(field(ParentCreateRequest::getPhoneNumber), gen -> gen.ints().range(100000000, 999999999).asString())
                .generate(field(ParentCreateRequest::getBirthDate), gen -> gen.temporal().date().past())
                .set(field(ParentCreateRequest::getChildrenIds), childrenIds)
                .create();
    }

    public StudentCreateRequest createStudentCreateRequest(YearbookEntity yearbook) {
        if(yearbook == null)
            yearbook = createYearbook(null, createTeacher(null));
        AuthorityEntity authority = dbHelper.fetchAuthorityByName("STUDENT").orElse(null);
        return Instancio.of(StudentCreateRequest.class)
                .set(field(StudentCreateRequest::getAuthority), Collections.singletonList(authority))
                .set(field(StudentCreateRequest::getParentsIds), Collections.emptyList())
                .set(field(StudentCreateRequest::getYearbookId), yearbook.getId())
                .generate(field(StudentCreateRequest::getEmail), gen -> gen.net().email())
                .generate(field(StudentCreateRequest::getPhoneNumber), gen -> gen.ints().range(100000000, 999999999).asString())
                .generate(field(StudentCreateRequest::getBirthDate), gen -> gen.temporal().date().past())
                .create();
    }

    public TeacherCreateRequest createTeacherCreateRequest(List<SubjectEntity> subjects) {
        if(CollectionUtils.isEmpty(subjects)) {
            subjects = new ArrayList<>();
            subjects.add(createSubject());
        }
        List<Integer> subjectIds = subjects.stream()
                .map(SubjectEntity::getId)
                .collect(Collectors.toList());
        AuthorityEntity authority = dbHelper.fetchAuthorityByName("TEACHER").orElse(null);

        return Instancio.of(TeacherCreateRequest.class)
                .set(field(TeacherCreateRequest::getTeachingSubjectsIds), subjectIds)
                .set(field(TeacherCreateRequest::getAuthority), Collections.singletonList(authority))
                .generate(field(TeacherCreateRequest::getEmail), gen -> gen.net().email())
                .generate(field(TeacherCreateRequest::getPhoneNumber), gen -> gen.ints().range(100000000, 999999999).asString())
                .generate(field(TeacherCreateRequest::getBirthDate), gen -> gen.temporal().date().past())
                .generate(field(TeacherCreateRequest::getEmploymentStartDate), gen -> gen.temporal().date().past())
                .set(field(TeacherCreateRequest::getEmploymentEndDate), null)
                .create();
    }

    public TeacherAvailabilityRequest createTeacherAvailabilityRequest() {
        return Instancio.of(TeacherAvailabilityRequest.class)
                .generate(field(TeacherAvailabilityRequest::getDayOfWeek), gen -> gen.ints().range(1, 7))
                .generate(field(TeacherAvailabilityRequest::getStartTime), gen -> gen.temporal().localTime().past()
                        .as(localTime -> localTime.withNano(0)))
                .generate(field(TeacherAvailabilityRequest::getEndTime), gen -> gen.temporal().localTime().future()
                        .as(localTime -> localTime.withNano(0)))
                .create();
    }

    public TeacherAvailabilityEntity createTeacherAvailabilityEntity(TeacherEntity teacher) {
        TeacherAvailabilityEntity availabilityEntity = Instancio.of(TeacherAvailabilityEntity.class)
                .set(field(TeacherAvailabilityEntity::getTeacher), teacher)
                .generate(field(TeacherAvailabilityEntity::getStartTime), gen -> gen.temporal().localTime().past()
                        .as(localTime -> localTime.withNano(0)))
                .generate(field(TeacherAvailabilityEntity::getEndTime), gen -> gen.temporal().localTime().future()
                        .as(localTime -> localTime.withNano(0)))
                .create();
        return teacherAvailabilityRepository.save(availabilityEntity);
    }

    public TeacherAvailabilityEntity createTeacherAvailabilityEntity(TeacherEntity teacher, DayOfWeek day) {
        TeacherAvailabilityEntity availabilityEntity = Instancio.of(TeacherAvailabilityEntity.class)
                .set(field(TeacherAvailabilityEntity::getTeacher), teacher)
                .set(field(TeacherAvailabilityEntity::getDayOfWeek), day)
                .generate(field(TeacherAvailabilityEntity::getStartTime), gen -> gen.temporal().localTime().past()
                        .as(localTime -> localTime.withNano(0)))
                .generate(field(TeacherAvailabilityEntity::getEndTime), gen -> gen.temporal().localTime().future()
                        .as(localTime -> localTime.withNano(0)))
                .create();
        return teacherAvailabilityRepository.save(availabilityEntity);
    }

    public PatchClassroomRequest createClassroomPatchRequest(List<EquipmentQuantity> eqToAdd, List<EquipmentQuantity> eqToRemove) {
        if(eqToAdd == null)
            eqToAdd = new ArrayList<>();
        if(eqToRemove == null)
            eqToRemove = new ArrayList<>();

        return Instancio.of(PatchClassroomRequest.class)
                .set(field(PatchClassroomRequest::getEquipmentIdsToAdd), eqToAdd)
                .set(field(PatchClassroomRequest::getEquipmentIdsToRemove), eqToRemove)
                .create();
    }

    public ClassroomRequest createClassroomRequest(List<EquipmentQuantity> eqs) {
        return Instancio.of(ClassroomRequest.class)
                .set(field(ClassroomRequest::getEquipments), eqs)
                .create();
    }

    public PatchGradeRequest createPatchGradeRequest(SubjectEntity subject) {
        StudentEntity student = createStudent(null, null);
        if(subject == null)
            subject = createSubject();
        GradeTypeEntity gradeType = createGradeType();
        GradeValueEntity gradeValue = createGradeValue(null);

        return Instancio.of(PatchGradeRequest.class)
                .set(field(PatchGradeRequest::getStudentId), student.getId())
                .set(field(PatchGradeRequest::getSubjectId), subject.getId())
                .set(field(PatchGradeRequest::getGradeTypeId), gradeType.getId())
                .set(field(PatchGradeRequest::getGradeValueId), gradeValue.getId())
                .create();
    }

    public AddScheduleRequest createAddScheduleRequest(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, TeacherEntity teacher) {
        if(dayOfWeek == null)
            dayOfWeek = Instancio.create(DayOfWeek.class);
        if(startTime == null)
            startTime = Instancio.gen().temporal().localTime()
                .range(LocalTime.of(1, 0), LocalTime.of(22, 0))
                .get();
        if(endTime == null)
            endTime = startTime.plusHours(1);

        SubjectEntity subject = createSubject();
        if(teacher == null)
            teacher = createTeacher(Collections.singletonList(subject));
        ClassroomEntity classroom = createClassroom();

        LocalDate effectiveDate = LocalDate.now();
        LocalDate expirationDate = effectiveDate.plusDays(2);

        return Instancio.of(AddScheduleRequest.class)
                .set(field(AddScheduleRequest::getSubjectId), teacher.getTeachingSubjects().iterator().next().getId())
                .set(field(AddScheduleRequest::getTeacherId), teacher.getId())
                .set(field(AddScheduleRequest::getClassroomId), classroom.getId())
                .set(field(AddScheduleRequest::getStartTime), startTime)
                .set(field(AddScheduleRequest::getEndTime), endTime)
                .set(field(AddScheduleRequest::getDayOfWeek), dayOfWeek.getValue())
                .set(field(AddScheduleRequest::getEffectiveDate), effectiveDate)
                .set(field(AddScheduleRequest::getExpirationDate), expirationDate)
                .generate(field(AddScheduleRequest::getRecurrenceType), gen -> gen.oneOf(ScheduleRecurrenceType.getProperRecurrenceTypes()))
                .create();
    }

    public PatchScheduleRequest createPatchScheduleRequest(DayOfWeek dayOfWeek, LocalTime startTime, LocalTime endTime, TeacherEntity teacher) {
        if(dayOfWeek == null)
            dayOfWeek = Instancio.create(DayOfWeek.class);
        if(startTime == null)
            startTime = Instancio.gen().temporal().localTime()
                    .range(LocalTime.of(1, 0), LocalTime.of(22, 0))
                    .get();
        if(endTime == null)
            endTime = startTime.plusHours(1);

        SubjectEntity subject = createSubject();
        if(teacher == null)
            teacher = createTeacher(Collections.singletonList(subject));
        ClassroomEntity classroom = createClassroom();
        YearbookEntity yearbook = createYearbook(Collections.singletonList(subject), teacher);

        LocalDate effectiveDate = LocalDate.now();
        LocalDate expirationDate = effectiveDate.plusDays(2);

        return Instancio.of(PatchScheduleRequest.class)
                .set(field(PatchScheduleRequest::getSubjectId), subject.getId())
                .set(field(PatchScheduleRequest::getTeacherId), teacher.getId())
                .set(field(PatchScheduleRequest::getClassroomId), classroom.getId())
                .set(field(PatchScheduleRequest::getYearbookId), yearbook.getId())
                .set(field(PatchScheduleRequest::getStartTime), startTime)
                .set(field(PatchScheduleRequest::getEndTime), endTime)
                .set(field(PatchScheduleRequest::getDayOfWeek), dayOfWeek.getValue())
                .set(field(PatchScheduleRequest::getEffectiveDate), effectiveDate)
                .set(field(PatchScheduleRequest::getExpirationDate), expirationDate)
                .create();
    }

    public AddScheduleVersionRequest createAddScheduleVersionRequest(YearbookEntity yearbook, boolean isActive) {
        if(yearbook == null)
            yearbook = createYearbook(null, null);

        return Instancio.of(AddScheduleVersionRequest.class)
                .set(field(AddScheduleVersionRequest::getYearbookId), yearbook.getId())
                .set(field(AddScheduleVersionRequest::isActive), isActive)
                .create();
    }

    public PatchScheduleVersionRequest createPatchScheduleVersionRequest(YearbookEntity yearbook, boolean isActive) {
        if(yearbook == null)
            yearbook = createYearbook(null, null);

        return Instancio.of(PatchScheduleVersionRequest.class)
                .set(field(PatchScheduleVersionRequest::isActive), isActive)
                .set(field(PatchScheduleVersionRequest::getYearbookId), yearbook.getId())
                .create();
    }

    public PatchYearbookRequest createPatchYearbookRequest(TeacherEntity teacher, List<SubjectEntity> subjectEntitiesToAdd, List<SubjectEntity> subjectEntitiesToRemove) {
        if(teacher == null)
            teacher = createTeacher(null);
        if(subjectEntitiesToAdd == null) {
            subjectEntitiesToAdd = new ArrayList<>();
            subjectEntitiesToAdd.add(createSubject());
        }
        if(subjectEntitiesToRemove == null) {
            subjectEntitiesToRemove = new ArrayList<>();
        }
        List<Integer> subjectIdsToAdd = subjectEntitiesToAdd.stream().map(SubjectEntity::getId).toList();
        List<Integer> subjectIdsToRemove = subjectEntitiesToRemove.stream().map(SubjectEntity::getId).toList();

        return Instancio.of(PatchYearbookRequest.class)
                .set(field(PatchYearbookRequest::getHeadTeacherId), teacher.getId())
                .set(field(PatchYearbookRequest::getMainCourseSubjectsIdsToAdd), subjectIdsToAdd)
                .set(field(PatchYearbookRequest::getMainCourseSubjectsIdsToRemove), subjectIdsToRemove)
                .create();
    }

    public ParentPatchRequest createParentPatchRequest(List<StudentEntity> childrenToAdd, List<StudentEntity> childrenToRemove) {
        if(childrenToAdd == null)
            childrenToAdd = new ArrayList<>();
        if(childrenToRemove == null)
            childrenToRemove = new ArrayList<>();
        List<Integer> childrenToAddIds = childrenToAdd.stream().map(StudentEntity::getId).toList();
        List<Integer> childrenToRemoveIds = childrenToRemove.stream().map(StudentEntity::getId).toList();

        return Instancio.of(ParentPatchRequest.class)
                .set(field(ParentPatchRequest::getChildrenIdsToAdd), childrenToAddIds)
                .set(field(ParentPatchRequest::getChildrenIdsToRemove), childrenToRemoveIds)
                .set(field(ParentPatchRequest::getAddress), null)
                .set(field(ParentPatchRequest::getAuthority), null)
                .generate(field(ParentPatchRequest::getEmail), gen -> gen.net().email())
                .generate(field(ParentPatchRequest::getPhoneNumber), gen -> gen.ints().range(100000000, 999999999).asString())
                .generate(field(ParentPatchRequest::getBirthDate), gen -> gen.temporal().date().past())
                .create();
    }

    public StudentPatchRequest createStudentPatchRequest(YearbookEntity yearbook) {
        if(yearbook == null)
            yearbook = createYearbook(null, null);
        return Instancio.of(StudentPatchRequest.class)
                .set(field(StudentPatchRequest::getYearbookId), yearbook.getId())
                .set(field(StudentPatchRequest::getParentIdsToAdd), null)
                .set(field(StudentPatchRequest::getParentIdsToRemove), null)
                .set(field(StudentPatchRequest::getAddress), null)
                .set(field(StudentPatchRequest::getAuthority), null)
                .generate(field(StudentPatchRequest::getEmail), gen -> gen.net().email())
                .generate(field(StudentPatchRequest::getPhoneNumber), gen -> gen.ints().range(100000000, 999999999).asString())
                .generate(field(StudentPatchRequest::getBirthDate), gen -> gen.temporal().date().past())
                .create();
    }

    public TeacherPatchRequest createTeacherPatchRequest(List<SubjectEntity> subjectsToAdd, List<SubjectEntity> subjectsToRemove) {
        if(subjectsToAdd.isEmpty()){
            subjectsToAdd = new ArrayList<>();
            subjectsToAdd.add(createSubject());
        }
        if(subjectsToRemove == null)
            subjectsToRemove = new ArrayList<>();
        List<Integer> subjectToAddIds = subjectsToAdd.stream().map(SubjectEntity::getId).toList();
        List<Integer> subjectToRemoveIds = subjectsToRemove.stream().map(SubjectEntity::getId).toList();

        return Instancio.of(TeacherPatchRequest.class)
                .set(field(TeacherPatchRequest::getTeachingSubjectsIdsToAdd), subjectToAddIds)
                .set(field(TeacherPatchRequest::getTeachingSubjectsIdsToRemove), subjectToRemoveIds)
                .set(field(TeacherPatchRequest::getAddress), null)
                .set(field(TeacherPatchRequest::getAuthority), null)
                .generate(field(TeacherPatchRequest::getEmail), gen -> gen.net().email())
                .generate(field(TeacherPatchRequest::getPhoneNumber), gen -> gen.ints().range(100000000, 999999999).asString())
                .generate(field(TeacherPatchRequest::getBirthDate), gen -> gen.temporal().date().past())
                .generate(field(TeacherPatchRequest::getEmploymentEndDate), gen -> gen.temporal().date().future())
                .create();
    }

    public ScheduleEntity fetchSchedule(Integer scheduleId) {
        Optional<ScheduleEntity> schedule = scheduleRepository.findById(scheduleId);
        return schedule.orElseGet(ScheduleEntity::new);
    }

    public List<ScheduleChangeLogEntity> fetchChangelogs(ScheduleEntity schedule) {
        if(schedule == null)
            return Collections.emptyList();
        return scheduleChangelogRepository.findAllBySchedule(schedule);
    }

    public AppConfigEntity createConfigEntity(AppConfigValueType valueType, String value, boolean isEditable, List<AuthorityEntity> authorities) {
        if(valueType == null)
            valueType = Instancio.create(AppConfigValueType.class);
        if(value == null)
            value = Instancio.create(String.class);
        if(authorities == null) {
            AuthorityEntity authority = dbHelper.fetchAuthorityByName("ADMINISTRATOR").orElse(null);
            authorities = List.of(authority);
        }

        AppConfigEntity entity = Instancio.of(AppConfigEntity.class)
                .set(field(AppConfigEntity::getId), null)
                .set(field(AppConfigEntity::getValueType), valueType)
                .set(field(AppConfigEntity::getValue), value)
                .set(field(AppConfigEntity::isEditable), isEditable)
                .set(field(AppConfigEntity::getEditableBy), authorities)
                .set(field(AppConfigEntity::getModifiedBy), createRandomAdminUser())
                .create();

        return configRepository.save(entity);
    }
}
