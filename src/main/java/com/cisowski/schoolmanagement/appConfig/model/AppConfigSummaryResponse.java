package com.cisowski.schoolmanagement.appConfig.model;

import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import lombok.Data;

import java.util.List;

@Data
public class AppConfigSummaryResponse {
    private Long id;
    private String key;
    private String value;
    private boolean isEditable;
    private List<AuthorityEntity> editableBy;
    private String description;

    @Override
    public String toString() {
        return "AppConfigEntity{" +
                "id=" + id +
                ", key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", isEditable=" + isEditable +
                '}';
    }
}
