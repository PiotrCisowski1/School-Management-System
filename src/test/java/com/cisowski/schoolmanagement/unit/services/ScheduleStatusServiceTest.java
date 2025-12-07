package com.cisowski.schoolmanagement.unit.services;

import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.timetable.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangeType;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleChangelog.ScheduleChangelogDto;
import com.cisowski.schoolmanagement.timetable.schedule.model.scheduleVersion.ScheduleVersionEntity;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleChangelogService;
import com.cisowski.schoolmanagement.timetable.schedule.service.ScheduleStatusService;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import com.cisowski.schoolmanagement.users.student.model.StudentEntity;
import com.cisowski.schoolmanagement.users.teacher.model.TeacherEntity;
import com.cisowski.schoolmanagement.yearbook.model.YearbookEntity;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.instancio.Select.field;
import static org.mockito.Mockito.*;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ScheduleStatusServiceTest {

    private ScheduleStatusService scheduleStatusService;

    @Mock
    private ScheduleChangelogService scheduleChangelogService;

    @Captor
    private ArgumentCaptor<ScheduleChangelogDto> changelogCaptor;

    @BeforeEach
    void setUp() {
        scheduleStatusService = new ScheduleStatusService(scheduleChangelogService);
    }

    @Test
    void createListWithUsersAffectedByChange_WhenScheduleIsNull_ShouldReturnEmptyList() {
        List<UserEntity> result = scheduleStatusService.createListWithUsersAffectedByChange(null);
        assertThat(result).isEmpty();
    }

    @Test
    void createListWithUsersAffectedByChange_WhenScheduleHasTeacherAndStudents_ShouldReturnAllUsers() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);

        StudentEntity student1 = Instancio.of(StudentEntity.class)
                .generate(field(StudentEntity::getParents), gen -> gen.collection().size(1))
                .create();
        StudentEntity student2 = Instancio.of(StudentEntity.class)
                .generate(field(StudentEntity::getParents), gen -> gen.collection().size(2))
                .create();

        YearbookEntity yearbook = Instancio.of(YearbookEntity.class)
                .set(field(YearbookEntity::getStudentsInYearbook), List.of(student1, student2))
                .create();

        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getYearbook), yearbook)
                .create();

        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getTeacher), teacher)
                .set(field(ScheduleEntity::getScheduleVersion), scheduleVersion)
                .create();

        List<UserEntity> result = scheduleStatusService.createListWithUsersAffectedByChange(schedule);

        assertThat(result).hasSize(1 + 2 + 3);
        assertThat(result).contains(teacher);
        assertThat(result).containsAll(student1.getParents());
        assertThat(result).containsAll(student2.getParents());
    }

    @Test
    void createListWithUsersAffectedByChange_WhenYearbookHasNoStudents_ShouldReturnOnlyTeacher() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        YearbookEntity yearbook = Instancio.of(YearbookEntity.class)
                .set(field(YearbookEntity::getStudentsInYearbook), Collections.emptyList())
                .create();

        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getYearbook), yearbook)
                .create();

        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getTeacher), teacher)
                .set(field(ScheduleEntity::getScheduleVersion), scheduleVersion)
                .create();

        List<UserEntity> result = scheduleStatusService.createListWithUsersAffectedByChange(schedule);

        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(teacher);
    }

    @Test
    void createListWithUsersAffectedByChange_WhenStudentHasNoParents_ShouldNotAddNullParents() {
        TeacherEntity teacher = Instancio.create(TeacherEntity.class);
        StudentEntity student = Instancio.of(StudentEntity.class)
                .set(field(StudentEntity::getParents), null)
                .create();

        YearbookEntity yearbook = Instancio.of(YearbookEntity.class)
                .set(field(YearbookEntity::getStudentsInYearbook), List.of(student))
                .create();

        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getYearbook), yearbook)
                .create();

        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getTeacher), teacher)
                .set(field(ScheduleEntity::getScheduleVersion), scheduleVersion)
                .create();

        List<UserEntity> result = scheduleStatusService.createListWithUsersAffectedByChange(schedule);

        assertThat(result).hasSize(2);
        assertThat(result).contains(teacher, student);
    }

    @Test
    void changeStatusToDeleted_WhenScheduleIsNull_ShouldDoNothing() {
        scheduleStatusService.changeStatusToDeleted(null);
        verifyNoInteractions(scheduleChangelogService);
    }

    @Test
    void changeStatusToDeleted_WhenScheduleIsValid_ShouldChangeStatusAndLogChange() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        scheduleStatusService.changeStatusToDeleted(schedule);

        assertThat(schedule.getStatus()).isEqualTo(ScheduleStatus.DELETED);
        verify(scheduleChangelogService).logChange(changelogCaptor.capture());

        ScheduleChangelogDto capturedDto = changelogCaptor.getValue();
        assertThat(capturedDto.getSchedule()).isEqualTo(schedule);
        assertThat(capturedDto.getChangeType()).isEqualTo(ScheduleChangeType.DELETED);
        assertThat(capturedDto.getReason()).isEqualTo("Schedule deleted permanently");
        assertThat(capturedDto.isAutomaticChange()).isFalse();
    }

    @Test
    void isAlreadyDeleted_WhenScheduleIsNull_ShouldReturnFalse() {
        boolean result = scheduleStatusService.isAlreadyDeleted(null);
        assertThat(result).isFalse();
    }

    @Test
    void isAlreadyDeleted_WhenScheduleStatusIsDeleted_ShouldReturnTrue() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.DELETED)
                .create();

        boolean result = scheduleStatusService.isAlreadyDeleted(schedule);

        assertThat(result).isTrue();
    }

    @Test
    void isAlreadyDeleted_WhenScheduleStatusIsNotDeleted_ShouldReturnFalse() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        boolean result = scheduleStatusService.isAlreadyDeleted(schedule);

        assertThat(result).isFalse();
    }

    @Test
    void filterDeletedSchedules_WhenSchedulesIsNull_ShouldReturnNull() {
        List<ScheduleEntity> result = scheduleStatusService.filterDeletedSchedules(null);
        assertThat(result).isNull();
    }

    @Test
    void filterDeletedSchedules_WhenSchedulesIsEmpty_ShouldReturnEmpty() {
        List<ScheduleEntity> schedules = Collections.emptyList();
        List<ScheduleEntity> result = scheduleStatusService.filterDeletedSchedules(schedules);
        assertThat(result).isEmpty();
    }

    @Test
    void filterDeletedSchedules_WhenSchedulesContainNull_ShouldFilterNull() {
        ScheduleEntity validSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();
        List<ScheduleEntity> schedules = Arrays.asList(null, validSchedule, null);

        List<ScheduleEntity> result = scheduleStatusService.filterDeletedSchedules(schedules);

        assertThat(result).containsExactly(validSchedule);
    }

    @Test
    void filterDeletedSchedules_WhenSchedulesContainDeletedAndNonDeleted_ShouldFilterDeleted() {
        ScheduleEntity deletedSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.DELETED)
                .create();

        ScheduleEntity scheduledSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        ScheduleEntity cancelledSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.CANCELLED)
                .create();

        List<ScheduleEntity> schedules = List.of(deletedSchedule, scheduledSchedule, cancelledSchedule);

        List<ScheduleEntity> result = scheduleStatusService.filterDeletedSchedules(schedules);

        assertThat(result).containsExactly(scheduledSchedule, cancelledSchedule);
        assertThat(result).doesNotContain(deletedSchedule);
    }

    @Test
    void markSchedulesAsDeleted_WhenSchedulesIsNull_ShouldDoNothing() {
        scheduleStatusService.markSchedulesAsDeleted(null);
        verifyNoInteractions(scheduleChangelogService);
    }

    @Test
    void markSchedulesAsDeleted_WhenSchedulesIsEmpty_ShouldDoNothing() {
        scheduleStatusService.markSchedulesAsDeleted(Collections.emptyList());
        verifyNoInteractions(scheduleChangelogService);
    }

    @Test
    void markSchedulesAsDeleted_WhenSchedulesContainNull_ShouldIgnoreNull() {
        ScheduleEntity validSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        Collection<ScheduleEntity> schedules = Arrays.asList(null, validSchedule, null);

        scheduleStatusService.markSchedulesAsDeleted(schedules);

        assertThat(validSchedule.getStatus()).isEqualTo(ScheduleStatus.DELETED);
        verify(scheduleChangelogService, times(1)).logChange(any(ScheduleChangelogDto.class));
    }

    @Test
    void markSchedulesAsDeleted_WhenSchedulesContainAlreadyDeleted_ShouldSkipDeleted() {
        ScheduleEntity deletedSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.DELETED)
                .create();

        ScheduleEntity scheduledSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        Collection<ScheduleEntity> schedules = List.of(deletedSchedule, scheduledSchedule);

        scheduleStatusService.markSchedulesAsDeleted(schedules);

        assertThat(scheduledSchedule.getStatus()).isEqualTo(ScheduleStatus.DELETED);
        assertThat(deletedSchedule.getStatus()).isEqualTo(ScheduleStatus.DELETED);
        verify(scheduleChangelogService, times(1)).logChange(any(ScheduleChangelogDto.class));
    }

    @Test
    void markSchedulesAsDeleted_WhenMultipleSchedules_ShouldChangeAllNonDeleted() {
        ScheduleEntity schedule1 = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        ScheduleEntity schedule2 = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.CANCELLED)
                .create();

        ScheduleEntity schedule3 = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        Collection<ScheduleEntity> schedules = List.of(schedule1, schedule2, schedule3);

        scheduleStatusService.markSchedulesAsDeleted(schedules);

        assertThat(schedule1.getStatus()).isEqualTo(ScheduleStatus.DELETED);
        assertThat(schedule2.getStatus()).isEqualTo(ScheduleStatus.DELETED);
        assertThat(schedule3.getStatus()).isEqualTo(ScheduleStatus.DELETED);
        verify(scheduleChangelogService, times(3)).logChange(any(ScheduleChangelogDto.class));
    }

    @Test
    void changeStatusToCanceled_WhenScheduleIsNull_ShouldDoNothing() {
        scheduleStatusService.changeStatusToCanceled(null, "Test reason");
        verifyNoInteractions(scheduleChangelogService);
    }

    @Test
    void changeStatusToCanceled_WhenScheduleIsValid_ShouldChangeStatusAndLogChange() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        String reason = "Lesson cancelled due to teacher illness";

        scheduleStatusService.changeStatusToCanceled(schedule, reason);

        assertThat(schedule.getStatus()).isEqualTo(ScheduleStatus.CANCELLED);
        verify(scheduleChangelogService).logChange(changelogCaptor.capture());

        ScheduleChangelogDto capturedDto = changelogCaptor.getValue();
        assertThat(capturedDto.getSchedule()).isEqualTo(schedule);
        assertThat(capturedDto.getChangeType()).isEqualTo(ScheduleChangeType.CANCELLED);
        assertThat(capturedDto.getOldValue()).isEqualTo("SCHEDULED");
        assertThat(capturedDto.getNewValue()).isEqualTo("CANCELLED");
        assertThat(capturedDto.getReason()).isEqualTo(reason);
        assertThat(capturedDto.isAutomaticChange()).isFalse();
    }

    @Test
    void changeStatusToCanceled_WhenScheduleHasDifferentStatus_ShouldCaptureOldStatus() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.COMPLETED)
                .create();

        scheduleStatusService.changeStatusToCanceled(schedule, "Test");

        verify(scheduleChangelogService).logChange(changelogCaptor.capture());

        ScheduleChangelogDto capturedDto = changelogCaptor.getValue();
        assertThat(capturedDto.getOldValue()).isEqualTo("COMPLETED");
        assertThat(capturedDto.getNewValue()).isEqualTo("CANCELLED");
    }

    @Test
    void integrationTest_MarkSchedulesAsDeletedAndThenFilter() {
        ScheduleEntity schedule1 = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        ScheduleEntity schedule2 = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.CANCELLED)
                .create();

        List<ScheduleEntity> schedules = new ArrayList<>(List.of(schedule1, schedule2));

        scheduleStatusService.markSchedulesAsDeleted(schedules);

        List<ScheduleEntity> filtered = scheduleStatusService.filterDeletedSchedules(schedules);
        assertThat(filtered).isEmpty();

        assertThat(schedule1.getStatus()).isEqualTo(ScheduleStatus.DELETED);
        assertThat(schedule2.getStatus()).isEqualTo(ScheduleStatus.DELETED);
    }

}
