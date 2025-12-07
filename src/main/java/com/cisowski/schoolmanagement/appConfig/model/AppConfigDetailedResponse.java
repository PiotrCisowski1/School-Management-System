package com.cisowski.schoolmanagement.appConfig.model;

import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AppConfigDetailedResponse {
    private Long id;
    private String key;
    private String value;
    private AppConfigValueType valueType;
    private String description;
    private boolean isEditable;
    private List<AuthorityEntity> editableBy;
    private String minValue;
    private String maxValue;
    private LocalDateTime createdAt;
    private Integer modifiedByUserId;
    private LocalDateTime modifiedAt;

    @Override
    public String toString() {
        return "AppConfigEntity{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", valueType=" + valueType +
                ", isEditable=" + isEditable +
                '}';
    }
}
