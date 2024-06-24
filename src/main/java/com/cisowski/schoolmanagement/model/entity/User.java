package com.cisowski.schoolmanagement.model.entity;

import jakarta.persistence.*;

import java.util.Collection;

@Entity
@Table(name = "users")
@DiscriminatorValue("user")
public class User extends Person {

      @Column(nullable = false)
    private String password;
    @Column(nullable = false, name = "enabled")
    private boolean isEnabled;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column
    private long phoneNumber;
    @Column(nullable = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_authorities",
    joinColumns = @JoinColumn(
            name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(
            name = "authority_id", referencedColumnName = "id"))
    private Collection<Authority> authority;


    public User(Integer id/*, String userName, String password*/, boolean isEnabled, Collection<Authority> authority) {
        this.id = id;
//        this.userName = userName;
//        this.password = password;
        this.isEnabled = isEnabled;
        this.authority = authority;
    }
    public User() {}

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    protected Integer getId() {
        return id;
    }

    protected void setId(Integer id) {
        this.id = id;
    }
    public Collection<Authority> getAuthority() {
        return authority;
    }

    public void setAuthority(Collection<Authority> authority) {
        this.authority = authority;
    }

    public long getPhoneNumber() {
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

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }

}
