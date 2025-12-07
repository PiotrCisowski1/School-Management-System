package com.cisowski.schoolmanagement.unit.helpers;

import com.cisowski.schoolmanagement.common.exception.type.SpecificationBrokenException;
import com.cisowski.schoolmanagement.schedule.model.ScheduleEntity;
import com.cisowski.schoolmanagement.schedule.model.ScheduleRecurrenceType;
import com.cisowski.schoolmanagement.schedule.model.ScheduleStatus;
import com.cisowski.schoolmanagement.schedule.model.scheduleVersion.ScheduleVersionEntity;
import org.instancio.Instancio;
import org.instancio.junit.InstancioExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.instancio.Select.field;

@ExtendWith({MockitoExtension.class, InstancioExtension.class})
class ScheduleConflictValidatorTest {

    private com.cisowski.schoolmanagement.schedule.helper.ScheduleConflictValidator validator;

    @BeforeEach
    void setUp() {
        validator = new com.cisowski.schoolmanagement.schedule.helper.ScheduleConflictValidator();
    }

    @Test
    void checkIfScheduleAlreadyAppointed_WhenScheduleVersionIsNull_ShouldThrowException() {
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);

        assertThatThrownBy(() -> validator.checkIfScheduleAlreadyAppointed(null, schedule))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given ScheduleVersion or Schedule is not a valid object");
    }

    @Test
    void checkIfScheduleAlreadyAppointed_WhenScheduleIsNull_ShouldThrowException() {
        ScheduleVersionEntity scheduleVersion = Instancio.create(ScheduleVersionEntity.class);

        assertThatThrownBy(() -> validator.checkIfScheduleAlreadyAppointed(scheduleVersion, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Given ScheduleVersion or Schedule is not a valid object");
    }

    @Test
    void checkIfScheduleAlreadyAppointed_WhenNoConflicts_ShouldNotThrowException() {
        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .generate(field(ScheduleVersionEntity::getSchedules), gen -> gen.collection().size(3))
                .create();

        ScheduleEntity newSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY)
                .set(field(ScheduleEntity::getStartTime), LocalTime.of(10, 0))
                .set(field(ScheduleEntity::getEndTime), LocalTime.of(11, 0))
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now().plusDays(1))
                .create();

        scheduleVersion.getSchedules().forEach(existing -> {
            existing.setDayOfWeek(DayOfWeek.TUESDAY);
            existing.setStartTime(LocalTime.of(9, 0));
            existing.setEndTime(LocalTime.of(10, 0));
        });

        assertThatNoException()
                .isThrownBy(() -> validator.checkIfScheduleAlreadyAppointed(scheduleVersion, newSchedule));
    }

    @Test
    void checkIfScheduleAlreadyAppointed_WhenTimeConflictSameDay_ShouldThrowException() {
        ScheduleEntity existingSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY)
                .set(field(ScheduleEntity::getStartTime), LocalTime.of(10, 0))
                .set(field(ScheduleEntity::getEndTime), LocalTime.of(11, 0))
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now())
                .set(field(ScheduleEntity::getExpirationDate), LocalDate.now().plusMonths(1))
                .create();

        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getSchedules), List.of(existingSchedule))
                .create();

        ScheduleEntity newSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY)
                .set(field(ScheduleEntity::getStartTime), LocalTime.of(10, 30))
                .set(field(ScheduleEntity::getEndTime), LocalTime.of(11, 30))
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now())
                .set(field(ScheduleEntity::getExpirationDate), LocalDate.now().plusMonths(1))
                .create();

        assertThatThrownBy(() -> validator.checkIfScheduleAlreadyAppointed(scheduleVersion, newSchedule))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("Schedule already appointed");
    }

    @Test
    void checkIfScheduleAlreadyAppointed_WhenDateRangeNoOverlap_ShouldNotThrowException() {
        ScheduleEntity existingSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY)
                .set(field(ScheduleEntity::getStartTime), LocalTime.of(10, 0))
                .set(field(ScheduleEntity::getEndTime), LocalTime.of(11, 0))
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now().plusDays(1))
                .set(field(ScheduleEntity::getExpirationDate), LocalDate.now().plusMonths(1))
                .create();

        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getSchedules), List.of(existingSchedule))
                .create();

        ScheduleEntity newSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY)
                .set(field(ScheduleEntity::getStartTime), LocalTime.of(10, 0))
                .set(field(ScheduleEntity::getEndTime), LocalTime.of(11, 0))
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now().plusMonths(2))
                .set(field(ScheduleEntity::getExpirationDate), LocalDate.now().plusMonths(3))
                .create();

        assertThatNoException()
                .isThrownBy(() -> validator.checkIfScheduleAlreadyAppointed(scheduleVersion, newSchedule));
    }

    @Test
    void checkIfScheduleAlreadyAppointed_WhenSameScheduleId_ShouldNotThrowException() {
        Integer sameId = 1;
        ScheduleEntity existingSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getId), sameId)
                .set(field(ScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY)
                .set(field(ScheduleEntity::getStartTime), LocalTime.of(10, 0))
                .set(field(ScheduleEntity::getEndTime), LocalTime.of(11, 0))
                .create();

        ScheduleVersionEntity scheduleVersion = Instancio.of(ScheduleVersionEntity.class)
                .set(field(ScheduleVersionEntity::getSchedules), List.of(existingSchedule))
                .create();

        ScheduleEntity sameSchedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getId), sameId)
                .set(field(ScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY)
                .set(field(ScheduleEntity::getStartTime), LocalTime.of(10, 0))
                .set(field(ScheduleEntity::getEndTime), LocalTime.of(11, 0))
                .create();

        assertThatNoException()
                .isThrownBy(() -> validator.checkIfScheduleAlreadyAppointed(scheduleVersion, sameSchedule));
    }

    @Test
    void checkScheduleCancellationPossible_WhenScheduleIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> validator.checkScheduleCancellationPossible(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("To check expiration Schedule cannot be null");
    }

    @Test
    void checkScheduleCancellationPossible_WhenScheduleIsCancelled_ShouldThrowException() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.CANCELLED)
                .create();

        assertThatThrownBy(() -> validator.checkScheduleCancellationPossible(schedule))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("cannot be canceled because is in status: CANCELLED");
    }

    @Test
    void checkScheduleCancellationPossible_WhenScheduleIsCompleted_ShouldThrowException() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.COMPLETED)
                .create();

        assertThatThrownBy(() -> validator.checkScheduleCancellationPossible(schedule))
                .isInstanceOf(SpecificationBrokenException.class)
                .hasMessageContaining("cannot be canceled because is in status: COMPLETED");
    }

    @Test
    void checkScheduleCancellationPossible_WhenScheduleIsScheduled_ShouldNotThrowException() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();

        assertThatNoException()
                .isThrownBy(() -> validator.checkScheduleCancellationPossible(schedule));
    }

    @Test
    void checkScheduleStatusInList_WhenScheduleIsNull_ShouldThrowException() {
        List<ScheduleStatus> statuses = List.of(ScheduleStatus.CANCELLED, ScheduleStatus.COMPLETED);

        assertThatThrownBy(() -> validator.checkScheduleStatusInList(null, statuses))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule cannot be null to check its status");
    }

    @Test
    void checkScheduleStatusInList_WhenExcludedStatusesIsNull_ShouldThrowException() {
        ScheduleEntity schedule = Instancio.create(ScheduleEntity.class);

        assertThatThrownBy(() -> validator.checkScheduleStatusInList(schedule, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Cannot check schedule status if exclusion list is not initialized");
    }

    @Test
    void checkScheduleStatusInList_WhenStatusInList_ShouldReturnTrue() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.CANCELLED)
                .create();
        List<ScheduleStatus> excludedStatuses = List.of(ScheduleStatus.CANCELLED, ScheduleStatus.DELETED);

        boolean result = validator.checkScheduleStatusInList(schedule, excludedStatuses);

        assertThat(result).isTrue();
    }

    @Test
    void checkScheduleStatusInList_WhenStatusNotInList_ShouldReturnFalse() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getStatus), ScheduleStatus.SCHEDULED)
                .create();
        List<ScheduleStatus> excludedStatuses = List.of(ScheduleStatus.CANCELLED, ScheduleStatus.DELETED);

        boolean result = validator.checkScheduleStatusInList(schedule, excludedStatuses);

        assertThat(result).isFalse();
    }

    @Test
    void isLessonAlreadyHeld_WhenScheduleIsNull_ShouldThrowException() {
        assertThatThrownBy(() -> validator.isLessonAlreadyHeld(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Schedule cannot be null to check if is already held");
    }

    @Test
    void isLessonAlreadyHeld_WhenSingleLessonInPast_ShouldReturnTrue() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.NONE)
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now().minusDays(1))
                .set(field(ScheduleEntity::getEndTime), LocalTime.now().minusHours(1))
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isTrue();
    }

    @Test
    void isLessonAlreadyHeld_WhenSingleLessonTodayEndTimePassed_ShouldReturnTrue() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.NONE)
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now())
                .set(field(ScheduleEntity::getEndTime), LocalTime.now().minusMinutes(30))
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isTrue();
    }

    @Test
    void isLessonAlreadyHeld_WhenSingleLessonInFuture_ShouldReturnFalse() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.NONE)
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now().plusDays(1))
                .set(field(ScheduleEntity::getEndTime), LocalTime.now().plusHours(2))
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isFalse();
    }

    @Test
    void isLessonAlreadyHeld_WhenSingleLessonNoEffectiveDate_ShouldReturnFalse() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.NONE)
                .set(field(ScheduleEntity::getEffectiveDate), null)
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isFalse();
    }

    @Test
    void isLessonAlreadyHeld_WhenRecurringLessonEffectiveDateInPast_ShouldReturnTrue() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.WEEKLY)
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now().minusDays(5))
                .set(field(ScheduleEntity::getDayOfWeek), DayOfWeek.MONDAY)
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isTrue();
    }

    @Test
    void isLessonAlreadyHeld_WhenRecurringLessonTodayEndTimePassed_ShouldReturnTrue() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.WEEKLY)
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now())
                .set(field(ScheduleEntity::getDayOfWeek), today)
                .set(field(ScheduleEntity::getEndTime), LocalTime.now().minusMinutes(15))
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isTrue();
    }

    @Test
    void isLessonAlreadyHeld_WhenRecurringLessonTodayNotStarted_ShouldReturnFalse() {
        DayOfWeek today = LocalDate.now().getDayOfWeek();
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.WEEKLY)
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now())
                .set(field(ScheduleEntity::getDayOfWeek), today)
                .set(field(ScheduleEntity::getEndTime), LocalTime.of(23, 59))
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isFalse();
    }

    @Test
    void isLessonAlreadyHeld_WhenRecurringLessonDifferentDay_ShouldReturnFalse() {
        DayOfWeek notToday = getNextDay(LocalDate.now().getDayOfWeek());
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.WEEKLY)
                .set(field(ScheduleEntity::getEffectiveDate), LocalDate.now())
                .set(field(ScheduleEntity::getExpirationDate), LocalDate.now().plusDays(2))
                .set(field(ScheduleEntity::getDayOfWeek), notToday)
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isFalse();
    }

    @Test
    void isLessonAlreadyHeld_WhenRecurringLessonNoEffectiveDate_ShouldReturnFalse() {
        ScheduleEntity schedule = Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getRecurrenceType), ScheduleRecurrenceType.WEEKLY)
                .set(field(ScheduleEntity::getEffectiveDate), null)
                .create();

        boolean result = validator.isLessonAlreadyHeld(schedule);

        assertThat(result).isFalse();
    }

    @Test
    void hasDayAndTimeConflict_WhenSameDayAndTimeOverlap_ShouldReturnTrue() throws Exception {
        ScheduleEntity existing = createSchedule(DayOfWeek.MONDAY,
                LocalTime.of(10, 0), LocalTime.of(11, 0));
        ScheduleEntity newSchedule = createSchedule(DayOfWeek.MONDAY,
                LocalTime.of(10, 30), LocalTime.of(11, 30));

        boolean result = callPrivateMethod(validator, "hasDayAndTimeConflict", existing, newSchedule);

        assertThat(result).isTrue();
    }

    @Test
    void hasDateOverlap_WhenDateRangesOverlap_ShouldReturnTrue() throws Exception {
        ScheduleEntity existing = createScheduleWithDates(
                LocalDate.now(), LocalDate.now().plusDays(10));
        ScheduleEntity newSchedule = createScheduleWithDates(
                LocalDate.now().plusDays(5), LocalDate.now().plusDays(15));

        boolean result = callPrivateMethod(validator, "hasDateOverlap", existing, newSchedule);

        assertThat(result).isTrue();
    }

    private ScheduleEntity createSchedule(DayOfWeek day, LocalTime start, LocalTime end) {
        return Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getDayOfWeek), day)
                .set(field(ScheduleEntity::getStartTime), start)
                .set(field(ScheduleEntity::getEndTime), end)
                .create();
    }

    private ScheduleEntity createScheduleWithDates(LocalDate effective, LocalDate expiration) {
        return Instancio.of(ScheduleEntity.class)
                .set(field(ScheduleEntity::getEffectiveDate), effective)
                .set(field(ScheduleEntity::getExpirationDate), expiration)
                .create();
    }

    private DayOfWeek getNextDay(DayOfWeek current) {
        return DayOfWeek.values()[(current.ordinal() + 1) % DayOfWeek.values().length];
    }

    @SuppressWarnings("unchecked")
    private <T> T callPrivateMethod(Object object, String methodName, Object... args) throws Exception {
        var method = object.getClass().getDeclaredMethod(methodName,
                Arrays.stream(args).map(Object::getClass).toArray(Class[]::new));
        method.setAccessible(true);
        return (T) method.invoke(object, args);
    }
}
