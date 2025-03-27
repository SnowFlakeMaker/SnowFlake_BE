package sookmyung.noonsongmaker.jwt;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sookmyung.noonsongmaker.Entity.User;

import java.util.Collection;
import java.util.List;

public class UserDetailsImpl implements UserDetails {
    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    public User getUser() {
        return this.user;
    }
}
