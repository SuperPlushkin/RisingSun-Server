package com.Sunrise.Controllers;

import com.Sunrise.DTO.Requests.LoginRequest;
import com.Sunrise.DTO.Requests.RegisterRequest;
import com.Sunrise.Services.AuthService;
import com.Sunrise.Services.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import jakarta.validation.constraints.Size;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(AuthService authService){
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        var result = authService.registerUser(request.getUsername(), request.getName(), request.getEmail(), request.getPassword());

        if (result.isSuccess())
        {
            log.info("User registered successfully --> {}", request.getUsername());
            return ResponseEntity.ok("User registered successfully. Check your mail to activate your account!!!");
        }
        else
        {
            log.warn(result.getErrorMessage());
            return ResponseEntity.badRequest().body(result.getErrorMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        String username = request.getUsername();
        String password = request.getPassword();

        var result = authService.authenticateUser(username, password, httpRequest);

        if (result.isSuccess())
        {
            log.info("User login successfully --> {}", request.getUsername());
            return ResponseEntity.ok(result.getJwtToken());
        }
        else
        {
            log.warn(result.getErrorMessage());
            return ResponseEntity.badRequest().body(result.getErrorMessage());
        }
    }

    @GetMapping(value = "/confirm", produces = "text/html; charset=UTF-8")
    public String confirmEmail(@RequestParam("type") String type, @RequestParam("token") @Size(min = 64, max = 64, message = "Token must be exactly 64 characters") String token) {

        var result = authService.confirmToken(type, token);

        String status = result.isSuccess()
            ? "<h3 style='color:green'>✅ Успех!</h3>"
            : "<h3 style='color:red'>❌ Ошибка</h3>";

        return """
           <!DOCTYPE html>
           <html lang="ru">
           <head>
               <meta charset="UTF-8">
               <title>Email Confirmation</title>
               <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
           </head>
           <body class="bg-light">
               <div class="container mt-5">
                   <div class="card shadow-sm">
                       <div class="card-body text-center">
                           %s
                           <p>%s</p>
                       </div>
                   </div>
               </div>
           </body>
           </html>
           """.formatted(status, result.getOperationText());
    }
}