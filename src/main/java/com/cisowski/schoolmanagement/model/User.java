package com.cisowski.schoolmanagement.model;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;

import javax.management.relation.Role;
import java.util.Collection;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "user_name", nullable = false, unique = true)
    private String userName;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private boolean enabled;
    @Column(nullable = false)
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "users_authorities",
    joinColumns = @JoinColumn(
            name = "user_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(
            name = "authority_id", referencedColumnName = "id"))
    private Collection<Authority> authority;


    public User(Integer id, String userName, String password, boolean enabled, Collection<Authority> authority) {
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.enabled = enabled;
        this.authority = authority;
    }
    public User() {}

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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                ", enabled=" + enabled +
                ", authority=" + authority.toString() +
                '}';
    }
}
