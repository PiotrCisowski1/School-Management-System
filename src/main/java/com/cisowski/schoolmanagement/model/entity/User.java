package com.cisowski.schoolmanagement.model.entity;

import com.cisowski.schoolmanagement.model.enums.Gender;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.BatchSize;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    @Column(nullable = false, name = "enabled")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Boolean isEnabled = true;
    @Column
    private Long phoneNumber;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;
    @Column
    private Date birthDate;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Gender gender;
    @Column(nullable = false)
    @OneToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_authorities",
    joinColumns = @JoinColumn(
            name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(
            name = "authority_id", referencedColumnName = "id"))
    private Collection<Authority> authority;



    public User(Integer id, String email, String password, boolean isEnabled, long phoneNumber,
                String firstName, String lastName, Date birthDate, Gender gender, Collection<Authority> authority,
                List<User> parents, List<Specialization> specializations, List<User> children) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.isEnabled = isEnabled;
        this.phoneNumber = phoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.gender = gender;
        this.authority = authority;
    }

    public User() {}

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Collection<Authority> getAuthority() {
        return authority;
    }

    public void setAuthority(Collection<Authority> authority) {
        this.authority = authority;
    }

    public Long getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(long phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }


    // Test method, will be changed with user entities
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isEnabled=" + isEnabled +
                ", phoneNumber=" + phoneNumber +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthDate=" + birthDate +
                ", gender=" + gender +
                ", authority=" + authority +
                '}';
    }
}
