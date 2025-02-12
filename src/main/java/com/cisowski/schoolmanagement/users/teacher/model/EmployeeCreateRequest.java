package com.cisowski.schoolmanagement.users.teacher.model;

import com.cisowski.schoolmanagement.users.common.model.BaseCreateUserRequest;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class EmployeeCreateRequest extends BaseCreateUserRequest {
    @NotNull(message = "EmploymentStartDate cannot be null")
    @DateTimeFormat
    private Date employmentStartDate;
    @DateTimeFormat
    @Future
    private Date employmentEndDate;

}
