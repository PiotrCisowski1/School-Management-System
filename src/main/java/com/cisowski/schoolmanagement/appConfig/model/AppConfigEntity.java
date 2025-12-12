package com.cisowski.schoolmanagement.appConfig.model;

import com.cisowski.schoolmanagement.users.common.model.AuthorityEntity;
import com.cisowski.schoolmanagement.users.common.model.UserEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "app_config")
@Data
public class AppConfigEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"key\"", unique = true, nullable = false, length = 100)
    private String key;

    @Column(nullable = false, length = 150)
    private String value;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private AppConfigValueType valueType;

    @Column(length = 200)
    private String description;

    @Column(nullable = false)
    private boolean isEditable;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "app_config_authorities",
            joinColumns = @JoinColumn(name = "app_config_key", referencedColumnName = "key"),
            inverseJoinColumns = @JoinColumn(name = "authority_id", referencedColumnName = "id"))
    private List<AuthorityEntity> editableBy;

    @Column(length = 30)
    private String minValue;

    @Column(length = 30)
    private String maxValue;

    @Column(nullable = false)
    @Setter(AccessLevel.NONE)
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "modified_by")
    private UserEntity modifiedBy;

    @Column(nullable = false)
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
