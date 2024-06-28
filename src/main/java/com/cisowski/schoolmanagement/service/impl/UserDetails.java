package com.cisowski.schoolmanagement.service.impl;

import com.cisowski.schoolmanagement.model.entity.Authority;
import com.cisowski.schoolmanagement.model.entity.Specialization;
import com.cisowski.schoolmanagement.model.entity.User;
import com.cisowski.schoolmanagement.model.enums.Gender;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class UserDetails implements org.springframework.security.core.userdetails.UserDetails {
    private final User user;


    public UserDetails(User user) {
        this.user = user;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(Authority authority: user.getAuthority()){
            authorities.add(new SimpleGrantedAuthority(authority.getAuthority()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
    public Integer getId() { return user.getId(); }
    public String getEmail() { return user.getEmail(); }
    public Long getPhoneNumber() { return user.getPhoneNumber(); }
    public String getFirstName() { return user.getFirstName(); }
    public String getLastName() { return user.getLastName(); }
    public Date getBirthDate() { return user.getBirthDate(); }
    public Gender getGender() { return user.getGender(); }
    public List<User> getParents() { return user.getParents(); }
    public List<User> getChildren() { return user.getChildren(); }
    public List<Specialization> getSpecializations() { return user.getSpecializations(); }

}
