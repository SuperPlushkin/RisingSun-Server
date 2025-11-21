package com.Sunrise.Services.StorageDataServices;

import com.Sunrise.Entities.User;
import com.Sunrise.Entities.Chat;
import com.Sunrise.Entities.VerificationToken;
import com.Sunrise.Services.StorageDataServices.CacheEntities.CacheChat;
import com.Sunrise.Services.StorageDataServices.CacheEntities.CacheChatMember;
import com.Sunrise.Services.StorageDataServices.Interfaces.ICacheStorageService;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class CacheService implements ICacheStorageService {

    // Основные кеши
    private final Map<String, Long> activeUserCache = new ConcurrentHashMap<>(); // jwt -> userId (только активные пользователи)
    private final Map<Long, User> userCache = new ConcurrentHashMap<>(); // userId -> User (все пользователи)
    private final Map<Long, CacheChat> chatInfoCache = new ConcurrentHashMap<>(); // chatId -> CacheChat (всех чатов)
    private final Map<String, VerificationToken> verificationTokenCache = new ConcurrentHashMap<>(); // token -> VerificationToken

    // Связующие кеши
    private final Map<Long, Set<Long>> userChatsCache = new ConcurrentHashMap<>(); // userId -> Set<chatId>
    private final Map<String, Long> personalChatCache = new ConcurrentHashMap<>(); // "userId_1:userId_2" -> chatId


    // ========== ACTIVE USER METHODS ==========


    // Основные методы
    public void saveActiveUser(String jwt, Long userId) {
        if (jwt != null && userId != null) {
            activeUserCache.put(jwt, userId);
        }
    }
    public void deleteActiveUser(String jwt) {
        activeUserCache.remove(jwt);
    }

    // Вспомогательные методы
    public Optional<User> getActiveUser(String jwt) {
        if (activeUserCache.get(jwt) instanceof Long userId)
            return Optional.ofNullable(userCache.get(userId));

        else return Optional.empty();
    }
    public Boolean existsActiveUser(String jwt) {
        return activeUserCache.containsKey(jwt);
    }


    // ========== USER METHODS ==========

    // Основные методы
    public User makeUser(String username, String name, String email, String hash_password, Boolean isEnabled) {
        var user = User.createUser(generateRandomId(), username, name, email, hash_password, isEnabled);

        saveUser(user);
        return user;
    }
    public void saveUser(User user) {
        if (user != null && user.getId() != null)
            userCache.put(user.getId(), user);
    }
    public void deleteUser(Long userId) {
        var user = userCache.remove(userId);
        activeUserCache.entrySet().removeIf(entry -> entry.getValue().equals(userId));

        if (user != null) {
            userChatsCache.remove(userId);
//             chatInfoCache.values().forEach(cacheChat -> cacheChat.removeMember(userId));
            clearAdminRightsForUser(userId);
        }
    }

    // Вспомогательные методы
    public void enableUser(Long userId){
        userCache.computeIfPresent(userId, (id, cacheChat) -> {
            cacheChat.setIsEnabled(true);
            return cacheChat;
        });
    }
    public void updateLastLogin(String username, LocalDateTime lastLogin) {
        if (username != null && lastLogin != null)
            userCache.values().stream()
                    .filter(user -> username.equals(user.getUsername()))
                    .findFirst()
                    .ifPresent(user -> user.setLastLogin(lastLogin));
    }
    public Optional<User> getUser(Long userId) {
        return Optional.ofNullable(userCache.get(userId));
    }
    public Optional<User> getUserByUsername(String username) {
        return userCache.values().stream().filter(user -> user.getUsername().equalsIgnoreCase(username)).findFirst();
    }
    public List<User> getFilteredUsersFromCache(String filter, int limit, int offset) {
        if (filter == null || filter.trim().isEmpty()) {
            return userCache.values().stream()
                    .skip(offset)
                    .limit(limit)
                    .collect(Collectors.toList());
        }

        String lowerFilter = filter.toLowerCase();
        return userCache.values().stream()
                .filter(user ->
                    (user.getUsername().toLowerCase().contains(lowerFilter)) ||
                    (user.getName().toLowerCase().contains(lowerFilter))
                )
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }
    public boolean existsUser(Long userId) {
        return userCache.containsKey(userId);
    }
    public Boolean existsUserByUsername(String username) {
        return userCache.values().stream().anyMatch(user -> user.getUsername().equalsIgnoreCase(username));
    }
    public Boolean existsUserByEmail(String email) {
        return userCache.values().stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }


    // ========== CHAT METHODS ==========


    // Основные методы
    public void saveChat(Chat chat) {
        if (chat != null && chat.getId() != null) {
            CacheChat cacheChat = new CacheChat(chat);
            chatInfoCache.put(chat.getId(), cacheChat);
        }
    }
    public void saveEmptyUserChats(Long userId) {
        userChatsCache.put(userId, ConcurrentHashMap.newKeySet());
    }
    public void deleteChat(Long chatId) {
        chatInfoCache.remove(chatId);
        userChatsCache.values().forEach(chatIds -> chatIds.remove(chatId));
        personalChatCache.entrySet().removeIf(entry -> entry.getValue().equals(chatId));
    }

    // Вспомогательные методы
    public Optional<Boolean> isGroupChat(Long chatId) {
        return Optional.ofNullable(chatInfoCache.get(chatId)).map(CacheChat::getIsGroup);
    }
    public boolean existsChat(Long chatId) {
        return chatInfoCache.containsKey(chatId);
    }
    public Optional<CacheChat> getChatInfo(Long chatId) {
        return Optional.ofNullable(chatInfoCache.get(chatId));
    }
    public Optional<String> getChatName(Long chatId) {
        return Optional.ofNullable(chatInfoCache.get(chatId)).map(CacheChat::getName);
    }
    public Optional<Long> getChatCreator(Long chatId) {
        return Optional.ofNullable(chatInfoCache.get(chatId)).map(CacheChat::getCreatedBy);
    }

    // Методы для работы с личными чатами
    public Chat makePersonalChat(Long userId1, Long userId2) {
        Long chatId = generateRandomId();

        Chat chat = Chat.createPersonalChat(chatId, userId1);
        chat.setId(chatId);

        // Сохраняем в кеш
        CacheChat cacheChat = new CacheChat(chatId, null, userId1, false);
        chatInfoCache.put(chatId, cacheChat);

        addUserToChat(userId1, chatId);
        addUserToChat(userId2, chatId);
        savePersonalChat(userId1, userId2, chatId);

        saveAdminRights(chatId, userId1, true);
        saveAdminRights(chatId, userId2, true);
        return chat;
    }
    public Optional<Long> findExistingPersonalChat(Long userId1, Long userId2) {
        String key = getPersonalChatKey(userId1, userId2);
        return Optional.ofNullable(personalChatCache.get(key));
    }
    public void savePersonalChat(Long userId1, Long userId2, Long chatId) {
        String key = getPersonalChatKey(userId1, userId2);
        personalChatCache.put(key, chatId);
    }

    // Методы для работы с групповыми чатами
    public Chat makeGroupChat(String name, Long createdBy, Set<Long> usersId) {
        Long id = generateRandomId();

        Chat chat = Chat.createGroupChat(id, name, createdBy);

        // Сохраняем в кеш
        CacheChat cacheChat = new CacheChat(id, name, createdBy, true);
        chatInfoCache.put(id, cacheChat);

        addUserToChat(createdBy, id);
        saveAdminRights(id, createdBy, true);

        for(Long userId : usersId) {
            addUserToChat(userId, id);
            saveAdminRights(id, userId, false);
        }
        return chat;
    }


    // ========== CHAT MEMBER METHODS ==========


    // Основные методы
    public void addUserToChat(Long chatId, Long userId) {
        userChatsCache.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(chatId);
        chatInfoCache.computeIfPresent(chatId, (id, cacheChat) -> {
            cacheChat.addMember(userId, false);
            return cacheChat;
        });
    }
    public void addUserToChatWith(Long chatId, Long userId, Boolean isAdmin) {
        addUserToChat(chatId, userId);
        saveAdminRights(chatId, userId, isAdmin);
    }
    public void removeUserFromChat(Long userId, Long chatId) {
        Set<Long> userChats = userChatsCache.get(userId);
        if (userChats != null)
            userChats.remove(chatId);

        chatInfoCache.computeIfPresent(chatId, (id, cacheChat) -> {
            cacheChat.removeMember(userId);
            return cacheChat;
        });

        deleteAdminRights(chatId, userId);
    }

    // Вспомогательные методы
    public Optional<List<Long>> getUserChats(Long userId) {
        Set<Long> chatIds = userChatsCache.get(userId);

        if (chatIds != null) {
            List<Long> chats = chatIds.stream()
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            return Optional.of(chats);
        }

        return Optional.empty();
    }
    public Set<Long> getChatMembers(Long chatId) {
        return chatInfoCache.get(chatId).getMemberIds();
    }
    public boolean isUserInChat(Long chatId, Long userId) {
        Set<Long> userChats = userChatsCache.get(userId);
        return userChats != null && userChats.contains(chatId);
    }


    // ========== VERIFICATION TOKEN METHODS ==========


    // Основные методы
    public VerificationToken makeVerificationToken(Long user_id, String tokenType) {
        Long id = generateRandomId();
        String token = generate64CharString();

        var verifToken = new VerificationToken(id, token, user_id, tokenType);

        // Сохраняем в кеш
        saveVerificationToken(verifToken);
        return verifToken;
    }
    public void saveVerificationToken(VerificationToken token) {
        if (token != null && token.getToken() != null)
            verificationTokenCache.put(token.getToken(), token);
    }
    public void deleteVerificationToken(String token) {
        verificationTokenCache.remove(token);
    }

    // Вспомогательные методы
    public Optional<VerificationToken> getVerificationToken(String token) {
        return Optional.ofNullable(verificationTokenCache.get(token));
    }
    public void cleanupExpiredVerificationTokens() {
        verificationTokenCache.entrySet().removeIf(entry -> entry.getValue().isExpired());
    }


    // ========== ADMIN RIGHTS METHODS ==========


    // Основные методы
    public void saveAdminRights(Long chatId, Long userId, Boolean isAdmin) {
        chatInfoCache.computeIfPresent(chatId, (id, cacheChat) -> {
            if (userId.equals(cacheChat.getCreatedBy()) && !isAdmin)
                return cacheChat; // Игнорируем попытку снять права у создателя

            if (cacheChat.hasMember(userId))
                cacheChat.setAdminRights(userId, isAdmin);
            return cacheChat;
        });
    }
    public void deleteAdminRights(Long chatId, Long userId) {
        chatInfoCache.computeIfPresent(chatId, (id, cacheChat) -> {
            if (!userId.equals(cacheChat.getCreatedBy()))
                cacheChat.setAdminRights(userId, false);
            return cacheChat;
        });
    }

    // Вспомогательные методы
    public Optional<Boolean> isChatAdmin(Long chatId, Long userId) {
        return Optional.ofNullable(chatInfoCache.get(chatId)).map(cacheChat -> cacheChat.isAdmin(userId));
    }
    public Set<Long> getChatAdmins(Long chatId) {
        CacheChat cacheChat = chatInfoCache.get(chatId);
        return cacheChat != null ? cacheChat.getAdminIds() : Collections.emptySet();
    }
    public void clearAdminRightsForChat(Long chatId) {
        chatInfoCache.computeIfPresent(chatId, (id, cacheChat) -> {
            Long creatorId = cacheChat.getCreatedBy();
            cacheChat.getMemberIds().forEach(memberId -> {
                if (!memberId.equals(creatorId)) // Не сбрасываем права создателя
                    cacheChat.setAdminRights(memberId, false);
            });
            return cacheChat;
        });
    }
    public void clearAdminRightsForUser(Long userId) {
        chatInfoCache.values().forEach(cacheChat -> {
            if (cacheChat.hasMember(userId) && !userId.equals(cacheChat.getCreatedBy())) // Не сбрасываем права создателя
                cacheChat.setAdminRights(userId, false);
        });
    }


    // ========== CACHE STATISTICS AND MANAGEMENT ==========


    // Основные методы
    public CacheStats getStats() {
        int totalChatMembers = chatInfoCache.values().stream().mapToInt(CacheChat::getMemberCount).sum();
        int totalAdminRights = chatInfoCache.values().stream()
                .mapToInt(
                    cacheChat -> (int)cacheChat.getChatMembers().values().stream().filter(CacheChatMember::getIsAdmin).count()
                ).sum();

        return new CacheStats(
            userCache.size(),
            chatInfoCache.size(),
            activeUserCache.size(),
            verificationTokenCache.size(),
            userChatsCache.size(),
            totalChatMembers,
            totalAdminRights
        );
    }
    public void clearUserCache() {
        userCache.clear();
        activeUserCache.clear();
        userChatsCache.clear();
        chatInfoCache.values().forEach(cacheChat -> cacheChat.getMemberIds().removeIf(memberId -> !memberId.equals(cacheChat.getCreatedBy())));
    }
    public void clearChatCache() {
        chatInfoCache.clear();
        userChatsCache.clear();
    }
    public void clearVerificationTokenCache() {
        verificationTokenCache.clear();
    }
    public void clearAll() {
        clearUserCache();
        clearChatCache();
        clearVerificationTokenCache();
    }
    public record CacheStats(int userCount, int chatCount, int activeUserCount, int verificationTokenCount, int userChatRelationsCount, int chatMemberRelationsCount, int adminRightsCount) { }


    // ========== HELPFUL FUNCTIONS ==========


    // Основные методы
    private Long generateRandomId() {
        SecureRandom random = new SecureRandom();
        return Math.abs(random.nextLong());
    }
    private String generate64CharString() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[48]; // 48 bytes = 64 base64 characters
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    private String getPersonalChatKey(Long userId1, Long userId2) {
        return Math.min(userId1, userId2) + ":" + Math.max(userId1, userId2);
    }
}