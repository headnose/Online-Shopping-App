package com.headnose.personalproject.controller;

import com.headnose.personalproject.dto.*;
import com.headnose.personalproject.entity.User;
import com.headnose.personalproject.exception.InvalidCredentialsException;
import com.headnose.personalproject.security.JwtUtil;
import com.headnose.personalproject.service.UserService;

import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService,
                          AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public String register(@Valid @RequestBody RegisterRequest request) {

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(request.getPassword());

        userService.register(user);

        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e) {
            throw new InvalidCredentialsException("Invalid login");
        }

        User user = userService.findByUsername(request.getUsername());

        String role = user.getRole();

        String token = jwtUtil.generateToken(
                user.getUsername(),
                role
        );

        return new AuthResponse(token);
    }
}
