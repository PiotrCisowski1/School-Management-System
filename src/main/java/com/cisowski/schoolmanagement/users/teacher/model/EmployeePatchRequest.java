package com.cisowski.schoolmanagement.users.teacher.model;

import com.cisowski.schoolmanagement.users.common.model.BasePatchUserRequest;
import jakarta.validation.constraints.Future;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

@EqualsAndHashCode(callSuper = true)
@Data
public abstract class EmployeePatchRequest extends BasePatchUserRequest {
    @DateTimeFormat
    private Date employmentStartDate;
    @Future
    @DateTimeFormat
    private Date employmentEndDate;

}
