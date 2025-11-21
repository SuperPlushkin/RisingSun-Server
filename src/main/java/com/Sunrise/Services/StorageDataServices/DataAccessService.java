package com.Sunrise.Services.StorageDataServices;

import com.Sunrise.DTO.DBResults.ChatStatsResult;
import com.Sunrise.DTO.DBResults.MessageResult;
import com.Sunrise.DTO.ServiceResults.UserDTO;
import com.Sunrise.Entities.Chat;
import com.Sunrise.Entities.User;
import com.Sunrise.Entities.VerificationToken;
import com.Sunrise.Services.StorageDataServices.CacheEntities.CacheChat;
import com.Sunrise.Services.StorageDataServices.Interfaces.IAsyncStorageService;
import com.Sunrise.Services.StorageDataServices.Interfaces.ICacheStorageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class DataAccessService {

    private final CacheService cacheService;
    private final IAsyncStorageService dbService;

    public DataAccessService(CacheService cacheService, DBService dbService) {
        this.cacheService = cacheService;
        this.dbService = dbService;
    }


    // ========== ACTIVE USER METHODS ==========


    // Основные методы
    public Optional<User> getActiveUser(String jwt) {
        return cacheService.getActiveUser(jwt);
    }
    public Boolean existsActiveUser(String jwt) {
        return cacheService.existsActiveUser(jwt);
    }
    public void deleteActiveUser(String jwt) {
        cacheService.deleteActiveUser(jwt);
    }


    // ========== USER METHODS ==========


    // Основные методы
    public Long makeUser(String username, String name, String email, String hash_password, Boolean isEnabled) {
        User user = cacheService.makeUser(username, name, email, hash_password, isEnabled);

        dbService.saveUserAsync(user);
        return user.getId();
    }
    public void enableUser(Long userId) {
        cacheService.enableUser(userId);
        dbService.enableUserAsync(userId);
    }
    public void saveUser(User user) {
        cacheService.saveUser(user);
        dbService.saveUserAsync(user);
    }
    public void deleteUser(Long userId) {
        cacheService.deleteUser(userId);
        dbService.deleteUserAsync(userId);
    }


    // Вспомогательные методы
    public Optional<User> getUser(Long userId) {
        return cacheService.getUser(userId);
    }
    public Optional<User> getUserByUsername(String username) {
        return cacheService.getUserByUsername(username);
    }
    public List<UserDTO> getFilteredUsers(String filter, int limit, int offset) {
        List<User> cachedUsers = cacheService.getFilteredUsersFromCache(filter, limit, offset);

        return cachedUsers.stream()
                .map(user -> new UserDTO(
                    user.getId(),
                    user.getUsername(),
                    user.getName()
                ))
                .toList();
    }
    public boolean notExistsUserById(Long userId) {
        return !cacheService.existsUser(userId);
    }
    public Boolean existsUserByUsername(String username) {
        return cacheService.existsUserByUsername(username);
    }
    public Boolean existsUserByEmail(String email) {
        return cacheService.existsUserByEmail(email);
    }
    public void updateLastLogin(String username, LocalDateTime lastLogin) {
        cacheService.updateLastLogin(username, lastLogin);
        dbService.updateLastLogin(username, lastLogin);
    }


    // ========== LOGIN HISTORY METHODS ==========


    // Основные методы
    public void addLoginHistory(Long userId, String ipAddress, String deviceInfo) {
        dbService.addLoginHistoryAsync(userId, ipAddress, deviceInfo);
    }  // БЕЗ КЭША


    // ========== CHAT METHODS ==========


    // Основные методы
    public Long makePersonalChatAndAddPeople(Long userId1, Long userId2) {
        Optional<Long> existedChatId = findPersonalChat(userId1, userId2);

        if(existedChatId.isPresent())
            return existedChatId.get();

        Chat chat = cacheService.makePersonalChat(userId1, userId2);

        CompletableFuture.runAsync(() -> {
            dbService.saveChat(chat);
            dbService.addUserToChat(userId1, chat.getId(), true);
            dbService.addUserToChat(userId2, chat.getId(), true);
        });
        return chat.getId();
    }
    public Long makeGroupChatAndAddPeople(String name, Long createdBy, Set<Long> usersId) {
        Chat chat = cacheService.makeGroupChat(name, createdBy, usersId);

        CompletableFuture.runAsync(() -> {
            dbService.saveChat(chat);
            dbService.addUserToChat(createdBy, chat.getId(), true);

            for (Long userId : usersId)
                dbService.addUserToChat(userId, chat.getId(), false);
        });
        return chat.getId();
    }
    public void deleteChat(Long chatId) {
        cacheService.deleteChat(chatId);
        dbService.deleteChatAsync(chatId);
    }


    // Вспомогательные методы
    public Optional<Long> findPersonalChat(Long userId1, Long userId2) {
        return cacheService.findExistingPersonalChat(userId1, userId2);
    }
    public Optional<Boolean> isGroupChat(Long chatId) {
        return cacheService.isGroupChat(chatId);
    }
    public Boolean existsChat(Long chatId) {
        return cacheService.existsChat(chatId);
    }
    public Optional<Boolean> isChatAdmin(Long chatId, Long userId) {
        return cacheService.isChatAdmin(chatId, userId);
    }
    public Optional<Long> findAnotherAdmin(Long chatId, Long excludeUserId) {
        Set<Long> admins = cacheService.getChatAdmins(chatId);
        if (!admins.isEmpty()) {
            for (Long adminId : admins) {
                if (!adminId.equals(excludeUserId))
                    return Optional.of(adminId);
            }
        }

        return Optional.empty();
    }
    public Integer getChatMemberCount(Long chatId) {
        return cacheService.getChatMembers(chatId).size();
    }


    // Методы для работы с сообщениями (пока что все с бд)
    public List<MessageResult> getChatMessages(Long chatId, Long userId, Integer limit, Integer offset) {
        return dbService.getChatMessages(chatId, userId, limit, offset);
    }
    public Integer getVisibleMessagesCount(Long chatId, Long userId) {
        return dbService.getVisibleMessagesCount(chatId, userId); // добавить кеш можно будет
    }
    public void markMessageAsRead(Long messageId, Long userId) {
        dbService.markMessageAsRead(messageId, userId);
    }


    // Методы для истории чатов (пока что все с бд)
    public Integer clearChatHistoryForAll(Long chatId, Long userId) {
        return dbService.clearChatHistoryForAll(chatId, userId);
    }
    public Integer clearChatHistoryForSelf(Long chatId, Long userId) {
        return dbService.clearChatHistoryForSelf(chatId, userId);
    }
    public Integer restoreChatHistoryForSelf(Long chatId, Long userId) {
        return dbService.restoreChatHistoryForSelf(chatId, userId);
    }
    public ChatStatsResult getChatClearStats(Long chatId, Long userId) {
        return dbService.getChatClearStats(chatId, userId);
    }


    // ========== CHAT MEMBER METHODS ==========

    public Set<Long> getChatMembers(Long chatId) {
        return cacheService.getChatMembers(chatId);
    }
    public Optional<List<Chat>> getUserChats(Long userId) {
        Optional<List<Long>> cachedChatIds = cacheService.getUserChats(userId);
        List<Chat> result = null;

        if (cachedChatIds.isPresent() && !cachedChatIds.get().isEmpty()) {
            result = new ArrayList<>();
            for (Long chatId : cachedChatIds.get()) {
                Optional<CacheChat> cacheChat = cacheService.getChatInfo(chatId);
                cacheChat.ifPresent(result::add);
            }
        }

        return Optional.ofNullable(result);
    }
    public Boolean isUserInChat(Long chatId, Long userId) {
        return cacheService.isUserInChat(chatId, userId);
    }
    public void addUserToChat(Long userId, Long chatId, Boolean isAdmin) {
        cacheService.addUserToChatWith(chatId, userId, isAdmin);
        dbService.addUserToChatAsync(userId, chatId, isAdmin);
    }
    public void removeUserFromChat(Long userId, Long chatId) {
        cacheService.removeUserFromChat(userId, chatId);
        dbService.removeUserFromChatAsync(userId, chatId);
    }

    public Optional<Long> getChatCreator(Long chatId) {
        return cacheService.getChatCreator(chatId);
    }
    public void updateChatCreator(Long chatId, Long newCreatorId) {
        cacheService.getChatInfo(chatId).ifPresent(cacheChat -> {
            cacheChat.setCreatedBy(newCreatorId);
            cacheChat.setAdminRights(newCreatorId, true);
        });

        dbService.updateChatCreatorAsync(chatId, newCreatorId);
//        dbService.ad(chatId, newCreatorId);
    } // КОЛХОЗ, ПОТОМ ИСПРАВЛЮ

    // ========== VERIFICATION TOKEN METHODS ==========


    // Основные методы
    public String makeVerificationToken(Long userId, String tokenType) {
        var verificationToken = cacheService.makeVerificationToken(userId, tokenType);

        dbService.saveVerificationTokenAsync(verificationToken);
        return verificationToken.getToken();
    }

    public void deleteVerificationToken(String token) {
        cacheService.deleteVerificationToken(token);
        dbService.deleteVerificationTokenAsync(token);
    }
    public void cleanupExpiredTokensAndWait() {
        cacheService.cleanupExpiredVerificationTokens();
        dbService.cleanupExpiredVerificationTokens();
    }


    // Вспомогательные методы
    public Optional<VerificationToken> getVerificationToken(String token) {
        return cacheService.getVerificationToken(token);
    }


    // ========== CACHE MANAGEMENT METHODS ==========


    // Основные методы
    public void clearUserCache() {
        cacheService.clearUserCache();
    }
    public void clearChatCache() {
        cacheService.clearChatCache();
    }
    public void clearVerificationTokenCache() {
        cacheService.clearVerificationTokenCache();
    }
    public void clearAllCache() {
        cacheService.clearAll();
    }
    public CacheService.CacheStats getCacheStats() {
        return cacheService.getStats();
    }


    // ========== UNIFIED INTERFACE ==========


    public ICacheStorageService getCache() {
        return cacheService;
    }
    public IAsyncStorageService getDatabase() {
        return dbService;
    }
}