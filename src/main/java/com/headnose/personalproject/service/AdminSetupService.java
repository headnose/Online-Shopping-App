package com.headnose.personalproject.service;

import com.headnose.personalproject.entity.User;
import com.headnose.personalproject.repository.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminSetupService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void createAdminIfNotExists() {

        if (userDao.findByUsername("admin") == null) {

            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@demo.com");
            admin.setPassword(passwordEncoder.encode("Admin123"));
            admin.setRole("ADMIN");

            userDao.save(admin);

            System.out.println("✅ Admin created.");
        } else {
            System.out.println("ℹ️ Admin already exists.");
        }
    }
}
