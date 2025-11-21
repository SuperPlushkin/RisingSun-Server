package com.Sunrise.Services.StorageDataServices;

import com.Sunrise.DTO.DBResults.ChatStatsResult;
import com.Sunrise.DTO.DBResults.MessageResult;
import com.Sunrise.Entities.LoginHistory;
import com.Sunrise.Entities.User;
import com.Sunrise.Entities.Chat;
import com.Sunrise.Entities.VerificationToken;
import com.Sunrise.Repositories.LoginHistoryRepository;
import com.Sunrise.Repositories.UserRepository;
import com.Sunrise.Repositories.ChatRepository;
import com.Sunrise.Repositories.VerificationTokenRepository;
import com.Sunrise.Services.StorageDataServices.Interfaces.IAsyncStorageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class DBService implements IAsyncStorageService {

    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final LoginHistoryRepository loginHistoryRepository;
    private final VerificationTokenRepository tokenRepository;

    public DBService(UserRepository userRepository, ChatRepository chatRepository, LoginHistoryRepository loginHistoryRepository, VerificationTokenRepository tokenRepository) {
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.loginHistoryRepository = loginHistoryRepository;
        this.tokenRepository = tokenRepository;
    }


    // ========== USER METHODS ==========


    // Основные методы
    public void saveUser(User user) {
        var gg = userRepository.save(user);
        System.out.println(gg);
    }
    public void saveUserAsync(User user) {
        CompletableFuture.runAsync(() -> {
            System.out.println("🔄 Async save started for user: " + user.getUsername());
            saveUser(user);
            System.out.println("✅ Async save completed for user: " + user.getUsername());
        }).exceptionally(ex -> {
            System.out.println("❌ Async save failed: " + ex.getMessage());
            return null;
        });
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    public void deleteUserAsync(Long userId) {
        CompletableFuture.runAsync(() -> deleteUser(userId));
    }

    public void updateLastLogin(String username, LocalDateTime lastLogin) {
        userRepository.updateLastLogin(username, lastLogin);
    }
    public void enableUser(Long userId) {
        userRepository.enableUser(userId);
    }
    public void enableUserAsync(Long userId) {
        CompletableFuture.runAsync(() -> enableUser(userId));
    }

    // Вспомогательные методы
    public Optional<User> getUser(Long userId) {
        return userRepository.findById(userId);
    }
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    public boolean existsUser(Long userId) {
        return userRepository.existsById(userId);
    }
    public Boolean existsUserByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    public Boolean existsUserByEmail(String email) {
        return userRepository.existsByEmail(email);
    }


    // ========== LOGIN HISTORY METHODS ==========


    // Основные методы
    public void saveLoginHistory(LoginHistory loginHistory) {
        loginHistoryRepository.save(loginHistory);
    }
    public void saveLoginHistoryAsync(LoginHistory loginHistory) {
        CompletableFuture.runAsync(() -> saveLoginHistory(loginHistory));
    }

    public void addLoginHistory(Long userId, String ipAddress, String deviceInfo) {
        loginHistoryRepository.addLoginHistory(userId, ipAddress, deviceInfo);
    }
    public void addLoginHistoryAsync(Long userId, String ipAddress, String deviceInfo) {
        CompletableFuture.runAsync(() -> addLoginHistory(userId, ipAddress, deviceInfo));
    }


    // ========== CHAT METHODS ==========


    // Основные методы
    public void saveChat(Chat chat) {
        chatRepository.save(chat);
    }
    public void saveChatAsync(Chat chat) {
        CompletableFuture.runAsync(() -> saveChat(chat));
    }

    public void deleteChat(Long chatId) {
        chatRepository.deleteChat(chatId);
    }
    public void deleteChatAsync(Long chatId) {
        CompletableFuture.runAsync(() -> deleteChat(chatId));
    }

    public void addUserToChat(Long userId, Long chatId, Boolean isAdmin) {
        chatRepository.upsertChatMember(chatId, userId, isAdmin);
    }
    public void addUserToChatAsync(Long userId, Long chatId, Boolean isAdmin) {
        CompletableFuture.runAsync(() -> addUserToChat(userId, chatId, isAdmin));
    }

    public void removeUserFromChat(Long userId, Long chatId) {
        chatRepository.leaveChat(chatId, userId);
    }
    public void removeUserFromChatAsync(Long userId, Long chatId) {
        CompletableFuture.runAsync(() -> removeUserFromChat(userId, chatId));
    }

    public void updateChatCreator(Long chatId, Long newCreatorId) {
        chatRepository.updateChatCreator(chatId, newCreatorId);
    }
    public void updateChatCreatorAsync(Long chatId, Long newCreatorId) {
        CompletableFuture.runAsync(() -> updateChatCreator(chatId, newCreatorId));
    }


    // Вспомогательные методы
    public Optional<Chat> getChat(Long chatId) {
        return chatRepository.findById(chatId);
    }
    public Optional<List<Chat>> getUserChats(Long userId) {
        return Optional.ofNullable(null);
    } // TODO: Реализовать надо метод
    public boolean existsChat(Long chatId) {
        return chatRepository.existsById(chatId);
    }
    public boolean isUserInChat(Long chatId, Long userId) {
        return chatRepository.isChatMember(chatId, userId);
    }


    // Методы для работы с личными чатами
    public Long findExistingPersonalChat(Long userId1, Long userId2) {
        return chatRepository.findExistingPersonalChat(userId1, userId2);
    }


    // Методы для работы с правами администратора
    public Boolean isChatAdmin(Long chatId, Long userId) {
        return chatRepository.isChatAdmin(chatId, userId);
    }
    public Long getChatCreator(Long chatId) {
        return chatRepository.getChatCreator(chatId);
    }
    public Long findAnotherAdmin(Long chatId, Long excludeUserId) {
        return chatRepository.findAnotherAdmin(chatId, excludeUserId);
    }


    // Методы для работы с участниками чата
    public Integer getChatMemberCount(Long chatId) {
        return chatRepository.getChatMemberCount(chatId);
    }


    // Методы для работы с сообщениями
    public List<MessageResult> getChatMessages(Long chatId, Long userId, Integer limit, Integer offset) {
        return chatRepository.getChatMessages(chatId, userId, limit, offset);
    }
    public void markMessageAsRead(Long messageId, Long userId) {
        chatRepository.markMessageAsRead(messageId, userId);
    }
    public Integer getVisibleMessagesCount(Long chatId, Long userId) {
        return chatRepository.getVisibleMessagesCount(chatId, userId);
    }


    // Методы для работы с историей чата
    public Integer clearChatHistoryForAll(Long chatId, Long userId) {
        return chatRepository.clearChatHistoryForAll(chatId, userId);
    }
    public Integer clearChatHistoryForSelf(Long chatId, Long userId) {
        return chatRepository.clearChatHistoryForSelf(chatId, userId);
    }
    public Integer restoreChatHistoryForSelf(Long chatId, Long userId) {
        return chatRepository.restoreChatHistoryForSelf(chatId, userId);
    }
    public ChatStatsResult getChatClearStats(Long chatId, Long userId) {
        return chatRepository.getChatClearStats(chatId, userId);
    }


    // ========== VERIFICATION TOKEN METHODS ==========


    // Основные методы
    public void saveVerificationToken(VerificationToken token) {
        tokenRepository.save(token);
    }
    public void saveVerificationTokenAsync(VerificationToken token) {
        CompletableFuture.runAsync(() -> saveVerificationToken(token));
    }

    public void deleteVerificationToken(String token) {
        tokenRepository.deleteByToken(token);
    }
    public void deleteVerificationTokenAsync(String token) {
        CompletableFuture.runAsync(() -> deleteVerificationToken(token));
    }

    public void cleanupExpiredVerificationTokens() {
        LocalDateTime now = LocalDateTime.now();
        tokenRepository.deleteByExpiryDateBefore(now);
    }
    public void cleanupExpiredVerificationTokensAsync() {
        CompletableFuture.runAsync(this::cleanupExpiredVerificationTokens);
    }

    // Вспомогательные методы
    public Optional<VerificationToken> getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }
}