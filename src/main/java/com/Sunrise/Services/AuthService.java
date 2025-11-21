package com.Sunrise.Services;

import com.Sunrise.DTO.ServiceResults.TokenConfirmationResult;
import com.Sunrise.DTO.ServiceResults.UserConfirmOperationResult;
import com.Sunrise.DTO.ServiceResults.UserInsertOperationResult;
import com.Sunrise.Entities.User;
import com.Sunrise.Entities.VerificationToken;
import com.Sunrise.JWT.JwtUtil;

import com.Sunrise.Services.StorageDataServices.DataAccessService;
import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthService {

    private final EmailService emailService;
    private final DataAccessService dataAccessService;
    private final JwtUtil jwtUtil;
    private final TransactionTemplate transactionTemplate;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(DataAccessService dataAccessService, JwtUtil jwtUtil, EmailService emailService, PlatformTransactionManager transactionManager) {
        this.dataAccessService = dataAccessService;
        this.jwtUtil = jwtUtil;
        this.emailService = emailService;

        this.transactionTemplate = new TransactionTemplate(transactionManager);
        this.transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        this.transactionTemplate.setReadOnly(false);
    }

    public UserInsertOperationResult registerUser(String username, String name, String email, String password) {
        return transactionTemplate.execute(status -> {
            try
            {
                // валидация данных
                if (username == null || username.trim().length() < 4)
                    return new UserInsertOperationResult(false, "Username must be at least 4 characters", null);

                if (name == null || name.trim().length() < 4)
                    return new UserInsertOperationResult(false, "Name must be at least 4 characters", null);

                if (password == null || password.length() < 8)
                    return new UserInsertOperationResult(false, "Password must be at least 8 characters", null);

                if (email == null || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
                    return new UserInsertOperationResult(false, "Invalid email format", null);

                // проверка на уникальность
                if (dataAccessService.existsUserByUsername(username.trim()))
                    return new UserInsertOperationResult(false, "Username already exists", null);

                if (dataAccessService.existsUserByEmail(email.toLowerCase()))
                    return new UserInsertOperationResult(false, "Email already exists", null);

                Long userId = dataAccessService.makeUser(username.trim(), name.trim(), email.toLowerCase(), passwordEncoder.encode(password), false);

                String token = dataAccessService.makeVerificationToken(userId, "email_confirmation");

                emailService.sendVerificationEmail(email, token);

                return new UserInsertOperationResult(true, null, token);
            }
            catch (Exception e)
            {
                status.setRollbackOnly();
                return new UserInsertOperationResult(false, "Registration failed due to server error", null);
            }
        });
    }
    public UserConfirmOperationResult authenticateUser(String username, String password, HttpServletRequest httpRequest) {
        return transactionTemplate.execute(status -> {
            try
            {
                Optional<User> userOpt = dataAccessService.getUserByUsername(username);

                if (userOpt.isEmpty())
                    return new UserConfirmOperationResult(false, "Invalid username or password", null);

                User user = userOpt.get();

                if (!user.getIsEnabled())
                    return new UserConfirmOperationResult(false, "Please verify your email first", null);

                if (passwordEncoder.matches(password, user.getHashPassword())) {
                    dataAccessService.updateLastLogin(username, LocalDateTime.now());
                    dataAccessService.addLoginHistory(user.getId(), extractClientIp(httpRequest), httpRequest.getHeader("User-Agent"));

                    return new UserConfirmOperationResult(true, null, jwtUtil.generateToken(username, user.getId()));
                }
                else return new UserConfirmOperationResult(false, "Invalid username or password", null);
            }
            catch (Exception e)
            {
                status.setRollbackOnly();
                return new UserConfirmOperationResult(false, "Authentication failed", null);
            }
        });
    }
    public TokenConfirmationResult confirmToken(String type, String token) {
        return transactionTemplate.execute(status -> {
            try
            {
                if (token == null || token.trim().isEmpty())
                    return new TokenConfirmationResult(false, "Token cannot be empty");

                Optional<VerificationToken> tokenOpt = dataAccessService.getVerificationToken(token);

                if (tokenOpt.isEmpty())
                    return new TokenConfirmationResult(false, "Invalid token");

                VerificationToken verificationToken = tokenOpt.get();

                if (!type.equals(verificationToken.getTokenType()))
                    return new TokenConfirmationResult(false, "Invalid token");

                if (verificationToken.getExpiryDate().isBefore(LocalDateTime.now()))
                {
                    dataAccessService.deleteVerificationToken(token);
                    return new TokenConfirmationResult(false, "Token expired");
                }

                dataAccessService.enableUser(verificationToken.getUser_id());
                dataAccessService.deleteVerificationToken(token);

                return new TokenConfirmationResult(true, "Email successfully verified");
            }
            catch (Exception e)
            {
                status.setRollbackOnly();
                return new TokenConfirmationResult(false, "Error during Token Confirmation: " + e.getMessage());
            }
        });
    }

    private String extractClientIp(HttpServletRequest request) {
        try
        {
            String xfHeader = request.getHeader("X-Forwarded-For");

            if (xfHeader != null && !xfHeader.isEmpty())
            {
                return xfHeader.split(",")[0].trim();
            }
            else return request.getRemoteAddr();
        }
        catch (Exception e)
        {
            return "unknown";
        }
    }
}
