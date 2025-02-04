package com.cisowski.schoolmanagement.model.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
