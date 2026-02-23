package com.headnose.personalproject.service;

import com.headnose.personalproject.constant.ApiMessages;
import com.headnose.personalproject.entity.User;
import com.headnose.personalproject.repository.UserDao;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void register(User user) {

        if (userDao.findByUsername(user.getUsername()) != null) {
            throw new RuntimeException(ApiMessages.USERNAME_ALREADY_EXISTS);
        }

        if (userDao.findByEmail(user.getEmail()) != null) {
            throw new RuntimeException(ApiMessages.EMAIL_ALREADY_EXISTS);
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");

        userDao.save(user);
    }

    @Transactional(readOnly = true)
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
