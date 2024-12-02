

package com.ing.credit_module.authentication;

import com.ing.credit_module.exception.UserNotFoundException;
import com.ing.credit_module.model.Role;
import com.ing.credit_module.model.User;
import com.ing.credit_module.service.IUserService;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class CreditUserDetailsService implements UserDetailsService {

    private IUserService userService;

    @Lazy
    @Autowired
    public CreditUserDetailsService(IUserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userService.findByUsername(username);
            return new CreditUserDetails(user, getAuthorities(user.getRoles()));
        } catch (UserNotFoundException exception) {
            throw new UsernameNotFoundException("User not found");
        }
    }

    private List<GrantedAuthority> getAuthorities(Collection<Role> roles) {
        return getGrantedAuthorities(roles);
    }

    private List<GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.getName()));
        }
        return authorities;
    }

    public static class CreditUserDetails implements UserDetails {

        @Getter
        private User user;
        @Getter
        private List<GrantedAuthority> authorities;

        public CreditUserDetails(User user, List<GrantedAuthority> authorities) {
            this.user = user;
            this.authorities = authorities;
        }

        @Override
        public String getPassword() {
            return user.getPasswordHash();
        }

        @Override
        public String getUsername() {
            return user.getUsername();
        }

    }
}