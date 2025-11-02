package com.Sunrise.Services;

import com.Sunrise.DTO.DB.InsertUserResult;
import com.Sunrise.DTO.ServiceAndController.TokenConfirmationResult;
import com.Sunrise.DTO.ServiceAndController.UserInsertOperationResult;
import com.Sunrise.Entities.User;
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
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository, LoginHistoryRepository loginHistoryRepository, VerificationTokenRepository verificationTokenRepository){
        this.userRepository = userRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.verificationTokenRepository = verificationTokenRepository;
    }

    public UserInsertOperationResult registerUser(String username, String name, String email, String password) {

        InsertUserResult result = userRepository.insertUserIfNotExists(username, name, email, passwordEncoder.encode(password));

        return new UserInsertOperationResult(result.getSuccess(), result.getErrorText(), result.getGeneratedToken());
    }
    @Transactional
    public boolean authenticateUser(String username, String password, HttpServletRequest httpRequest) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        String ipAddress = extractClientIp(httpRequest);
        String userAgent = httpRequest.getHeader("User-Agent");

        if (userOpt.isPresent()) {
            User user = userOpt.get();

            if (passwordEncoder.matches(password, user.getHashPassword())) {
                userRepository.updateLastLogin(username, LocalDateTime.now());
                loginHistoryRepository.addLoginHistory(user.getId(), ipAddress, userAgent);
                return true;
            }
        }

        return false;
    }
    @Transactional
    public TokenConfirmationResult confirmToken(String type, String token) {

        var result = switch (type) {
            case "email_confirmation" -> verificationTokenRepository.confirmUserByToken(token);
            default -> null;
        };

        if(result == null)
            return new TokenConfirmationResult(false, "Not valid type of token");

        boolean success = result.getSuccess();

        String message = success
            ? type + " Token was successfully confirmed!!!"
            : result.getErrorText();

        return new TokenConfirmationResult(success, message);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0]; // если прокси, берём первый IP
        }
        return request.getRemoteAddr(); // fallback
    }
}
