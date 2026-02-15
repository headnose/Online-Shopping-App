package com.headnose.personalproject.service;

import com.headnose.personalproject.entity.TestUser;
import com.headnose.personalproject.repository.TestUserDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class TestUserService {

    private final TestUserDao dao;

    public TestUserService(TestUserDao dao) {
        this.dao = dao;
    }

    public void createUser(String name) {
        dao.save(new TestUser(null, name));
    }

    public List<TestUser> getAllUsers() {
        return dao.findAll();
    }
}
