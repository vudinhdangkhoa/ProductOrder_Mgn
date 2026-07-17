package com.example.demo.security;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.demo.entity.Permission;
import com.example.demo.entity.RolePermission;
import com.example.demo.entity.User;

import lombok.Getter;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Boolean active;
    private final Collection<? extends GrantedAuthority> authorities;

    public CustomUserDetails(User user) {

        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.active = !user.getIsDeleted();

        Set<GrantedAuthority> auths = new HashSet<>();

        // ROLE_ADMIN
        auths.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().getNameRole().name()
                )
        );

        // USER_CREATE
        // USER_DELETE
        // ORDER_APPROVE

        for (RolePermission rp : user.getRole().getRolePermissions()) {

            Permission permission = rp.getPermission();

            auths.add(
                    new SimpleGrantedAuthority(
                            permission.getNamePermission()
                    )
            );
        }

        this.authorities = auths;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
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
        return active;
    }
}