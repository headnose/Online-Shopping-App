package com.headnose.personalproject.security;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.entity.User;
import com.headnose.personalproject.repository.UserDao;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserDao userDao;

    public CustomUserDetailsService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = userDao.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException(ApiMessages.USER_NOT_FOUND);
        }

        // 🔥 Updated constructor call
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole()
        );
    }
}