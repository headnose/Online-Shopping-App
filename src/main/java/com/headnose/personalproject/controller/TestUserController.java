package com.headnose.personalproject.controller;

import com.headnose.personalproject.entity.TestUser;
import com.headnose.personalproject.service.TestUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/test")
public class TestUserController {

    private final TestUserService service;

    public TestUserController(TestUserService service) {
        this.service = service;
    }

    @PostMapping("/{name}")
    public String create(@PathVariable String name) {
        System.out.println(">>> CONTROLLER HIT with: " + name);
        service.createUser(name);
        return "User saved!";
    }

    @GetMapping
    public List<TestUser> getAll() {
        return service.getAllUsers();
    }


}
