package com.RisingSun.Controllers;

import com.RisingSun.DTO.LoginRequest;
import com.RisingSun.DTO.RegisterRequest;
import com.RisingSun.JWT.JwtUtil;
import com.RisingSun.Services.AuthService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtUtil jwtUtil;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        var result = authService.registerUser(request.getUsername(), request.getName(), request.getPassword());

        if (result.success()) {
            return ResponseEntity.ok("User registered successfully");
        }
        else return ResponseEntity.badRequest().body(result.error());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        String username = request.getUsername();
        String password = request.getPassword();

        Boolean successful_login = authService.authenticateUser(username, password, httpRequest);

        if (successful_login)
        {
            String token = jwtUtil.generateToken(username);
            return ResponseEntity.ok(token);
        }
        else return ResponseEntity.badRequest().body("Invalid credentials");
    }
}