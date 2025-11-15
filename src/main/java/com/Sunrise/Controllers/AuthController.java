package com.Sunrise.Controllers;

import com.Sunrise.DTO.Requests.LoginRequest;
import com.Sunrise.DTO.Requests.RegisterRequest;
import com.Sunrise.DTO.ServiceResults.TokenConfirmationResult;
import com.Sunrise.Subclasses.MyException;
import com.Sunrise.Services.AuthService;
import com.Sunrise.Services.EmailService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/app/auth")
public class AuthController {

    private final AuthService authService;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthController(AuthService authService, EmailService emailService){
        this.authService = authService;
        this.emailService = emailService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {

        var result = authService.registerUser(request.getUsername(), request.getName(), request.getEmail(), request.getPassword());

        if (result.isSuccess())
        {
            emailService.sendVerificationEmail(request.getEmail(), result.getToken());
            return ResponseEntity.ok("User registered successfully. Check your mail to activate your account!!!");
        }
        else throw new MyException(result.getInfoMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {

        String username = request.getUsername();
        String password = request.getPassword();

        var jwt_token = authService.authenticateUser(username, password, httpRequest);

        if (jwt_token.isPresent())
        {
            return ResponseEntity.ok(jwt_token.get());
        }
        else return ResponseEntity.badRequest().body("Invalid credentials");
    }

    @GetMapping(value = "/confirm", produces = "text/html; charset=UTF-8")
    public String confirmEmail(@RequestParam("type") String type, @RequestParam("token") String token) {

        TokenConfirmationResult result = authService.confirmToken(type, token);

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