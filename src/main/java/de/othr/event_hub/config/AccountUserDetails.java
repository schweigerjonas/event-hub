package de.othr.event_hub.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import de.othr.event_hub.model.Authority;
import de.othr.event_hub.model.User;

public class AccountUserDetails implements UserDetails {
    private static final long serialVersionUID = 1L;
    private String username;
    private String email;
    private String password;
    private boolean active;
    private List<GrantedAuthority> authorities;
    private User user;

    public AccountUserDetails(User user) {
        this.user = user;
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.active = (user.getActive() > 0) ? true : false;
        List<Authority> userAuthorities = (List<Authority>) user.getAuthorities();

        authorities = new ArrayList<>();

        for (int i = 0; i < userAuthorities.size(); i++) {
            authorities.add(new SimpleGrantedAuthority(userAuthorities.get(i).getDescription().toUpperCase()));
        }
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public List<GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isEnabled() {
        // TODO Auto-generated method stub
        return this.active;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public User getUser() {
        return user;
    }

    public String getEmail() {
        return email;
    }
}
