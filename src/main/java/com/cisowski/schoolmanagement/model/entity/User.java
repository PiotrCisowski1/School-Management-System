package com.cisowski.schoolmanagement.model.entity;

import com.cisowski.schoolmanagement.model.enums.Gender;
import jakarta.persistence.*;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, name = "enabled")
    private Boolean isEnabled;
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
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_authorities",
    joinColumns = @JoinColumn(
            name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(
            name = "authority_id", referencedColumnName = "id"))
    private Collection<Authority> authority;


    // Optional fields referencing particular user's(Teacher, Student, Parent...) data
    // this will be changed in future versions

    /*
               STUDENT DATA
     */
    @OneToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "students_parents", joinColumns = @JoinColumn(name = "parent_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "student_id", referencedColumnName = "id"))
    private List<User> parents;


    /*
                TEACHER DATA
     */
    @Column(nullable = false)
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(name = "teachers_specializations",
            joinColumns = @JoinColumn(
                    name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(
                    name = "specialization_id", referencedColumnName = "id"))
    private List<Specialization> specializations;

    /*
                STUDENT DATA
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "students_parents", joinColumns = @JoinColumn(name = "student_id",referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id"))
    private List<User> children;


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
        this.parents = parents;
        this.specializations = specializations;
        this.children = children;
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

    public List<User> getParents() {
        return parents;
    }

    public void setParents(List<User> parents) {
        this.parents = parents;
    }

    public List<Specialization> getSpecializations() {
        return specializations;
    }

    public void setSpecializations(List<Specialization> specializations) {
        this.specializations = specializations;
    }

    public List<User> getChildren() {
        return children;
    }

    public void setChildren(List<User> children) {
        this.children = children;
    }

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
                ", parents=" + parents +
                ", specializations=" + specializations +
                ", children=" + children +
                '}';
    }
}
