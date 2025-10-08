package com.RisingSun.Controllers;

import com.RisingSun.DTO.LoginRequest;
import com.RisingSun.JWT.JwtUtil;
import com.RisingSun.Entities.User;
import com.RisingSun.Services.AuthService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/app/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody User user) {
        try
        {
            String result = authService.registerUser(user);

            if ("User already exists".equals(result)) {
                return ResponseEntity.badRequest().body("User already exists");
            }
            else return ResponseEntity.ok("User registered successfully");

        }
        catch (Exception e)
        {
            return ResponseEntity.badRequest().body("Registration failed: " + e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {

        Optional<String> username = authService.authenticateUser(request.getUsername(), request.getPassword());

        if (username.isPresent())
        {
            String token = jwtUtil.generateToken(username.get());
            return ResponseEntity.ok(token);
        }
        else return ResponseEntity.badRequest().body("Invalid credentials");
    }
}