package com.Sunrise.Services;

import com.Sunrise.DTO.DBResults.InsertUserResult;
import com.Sunrise.DTO.ServiceResults.TokenConfirmationResult;
import com.Sunrise.DTO.ServiceResults.UserInsertOperationResult;
import com.Sunrise.Entities.User;
import com.Sunrise.JWT.JwtUtil;
import com.Sunrise.Repositories.LoginHistoryRepository;
import com.Sunrise.Repositories.UserRepository;
import com.Sunrise.Repositories.VerificationTokenRepository;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, LoginHistoryRepository loginHistoryRepository, VerificationTokenRepository verificationTokenRepository, JwtUtil jwtUtil){
        this.userRepository = userRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.verificationTokenRepository = verificationTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public UserInsertOperationResult registerUser(String username, String name, String email, String password) {

        InsertUserResult result = userRepository.insertUserIfNotExists(username, name, email, passwordEncoder.encode(password));

        return new UserInsertOperationResult(result.getSuccess(), result.getErrorText(), result.getGeneratedToken());
    }
    @Transactional
    public Optional<String> authenticateUser(String username, String password, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        String ipAddress = extractClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(password, user.getHashPassword())) {
                userRepository.updateLastLogin(username, LocalDateTime.now());
                loginHistoryRepository.addLoginHistory(user.getId(), ipAddress, userAgent);

                return Optional.of(jwtUtil.generateToken(username, user.getId()));
            }
        }

        return Optional.empty();
    }
    @Transactional
    public TokenConfirmationResult confirmToken(String type, String token) {

        var result = switch (type) {
            case "email_confirmation" -> verificationTokenRepository.confirmUserByToken(token);
            default -> null;
        };

        if(result != null)
        {
            boolean success = result.getSuccess();

            String message = success
                ? type + " Token was successfully confirmed!!!"
                : result.getErrorText();

            return new TokenConfirmationResult(success, message);
        }
        else return new TokenConfirmationResult(false, "Not valid type of token");
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0]; // если прокси, берём первый IP
        }
        return request.getRemoteAddr(); // fallback
    }
}
