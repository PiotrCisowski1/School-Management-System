package com.cisowski.schoolmanagement.timetable.attendance.model;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MarkAttendanceRequest {

    @NotNull
    @NotEmpty
    List<Integer> studentIds;

    @NotNull
    AttendanceStatus status;
}
