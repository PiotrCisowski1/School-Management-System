package com.cisowski.schoolmanagement.appConfig.model;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class AppConfigUpdateRequest {
    @Size(max = 150)
    private String value;
    @Size(max = 200)
    private String description;
    private List<Integer> authoritiesToAddAsEditableBy;
    private List<Integer> authoritiesToRemoveAsEditableBy;
    @Size(max = 30)
    private String minValue;
    @Size(max = 30)
    private String maxValue;

    @AssertTrue
    private boolean notEmptyValue() {
        if(value != null)
            return StringUtils.isNotEmpty(value);
        return true;
    }

    @Override
    public String toString() {
        return "AppConfigUpdateRequest{" +
                "description='" + (description != null ? description : "*empty description*") + '\'' +
                ", maxValue='" + (maxValue != null ? maxValue : "*empty minValue*") + '\'' +
                ", minValue='" + (minValue != null ? minValue : "*empty maxValue*") + '\'' +
                ", value='" + (value != null ? value : "*empty value*") +
                '}';
    }
}
