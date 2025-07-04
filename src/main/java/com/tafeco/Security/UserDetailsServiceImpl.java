package com.tafeco.Security;

import com.tafeco.Models.DAO.IUserDAO;
import com.tafeco.Models.Entity.RoleUser;
import com.tafeco.Models.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserDAO userDAO;

    public UserDetailsServiceImpl(IUserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDAO.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + email));

        boolean temporaryPassword = user.getTempPasswordExpiration() != null &&
                user.getTempPasswordExpiration().isAfter(LocalDateTime.now());

        return new CustomUserDetails(
                user.getEmail(),
                user.getPassword(),
                user.isActive(),
                mapRolesToAuthorities(user.getRoles()),
                temporaryPassword
        );
    }


    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<RoleUser> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole()))
                .collect(Collectors.toList());
    }
}

