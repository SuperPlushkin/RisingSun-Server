package com.RisingSun.Services;

import com.RisingSun.Entities.User;
import com.RisingSun.Repositories.LoginHistoryRepository;
import com.RisingSun.Repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    public record OperationResult(boolean success, String error) {}

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public OperationResult registerUser(String username, String password) {

        var success = userRepository.insertUserIfNotExists(username, passwordEncoder.encode(password));

        return new OperationResult(success, success ? null : "User already exists");
    }

    public Boolean authenticateUser(String username, String password, HttpServletRequest httpRequest) {
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

    private String extractClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader != null && !xfHeader.isEmpty()) {
            return xfHeader.split(",")[0]; // если прокси, берём первый IP
        }
        return request.getRemoteAddr(); // fallback
    }
}
