package com.Sunrise.Repositories;

import com.Sunrise.DTO.DBResults.ChatCreationResult;
import com.Sunrise.DTO.DBResults.ChatStatsResult;
import com.Sunrise.DTO.DBResults.ClearHistoryResult;
import com.Sunrise.DTO.DBResults.IDBResult;
import com.Sunrise.DTO.DBResults.MessageResult;
import com.Sunrise.Entities.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {

    // ========== УДАЛЕНИЕ И ВОССТАНОВЛЕНИЕ ИСТОРИИ ==========

    @Query(value = "SELECT success, error_text, deleted_count as affected_count " +
            "FROM clear_chat_history_for_all(:chatId, :userId)", nativeQuery = true)
    ClearHistoryResult clearChatHistoryForAll(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query(value = "SELECT success, error_text, hidden_count as affected_count " +
            "FROM clear_chat_history_for_self(:chatId, :userId)", nativeQuery = true)
    ClearHistoryResult clearChatHistoryForSelf(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Query(value = "SELECT success, error_text, restored_count as affected_count " +
            "FROM restore_chat_history_for_self(:chatId, :userId)", nativeQuery = true)
    ClearHistoryResult restoreChatHistoryForSelf(@Param("chatId") Long chatId, @Param("userId") Long userId);

    // ========== СТАТИСТИКА ЧАТА ==========

    @Query(value = "SELECT total_messages, deleted_for_all, hidden_by_user, can_clear_for_all " +
            "FROM get_chat_clear_stats(:chatId, :userId)", nativeQuery = true)
    ChatStatsResult getChatClearStats(@Param("chatId") Long chatId, @Param("userId") Long userId);

    // ========== СООБЩЕНИЯ ЧАТА ==========

    @Query(value = "SELECT message_id, sender_id, sender_username, text, sent_at, is_deleted, read_count, is_hidden_by_user " +
            "FROM get_chat_messages(:chatId, :userId, :limit, :offset)", nativeQuery = true)
    List<MessageResult> getChatMessages(@Param("chatId") Long chatId, @Param("userId") Long userId, @Param("limit") Integer limit, @Param("offset") Integer offset);

    // ========== СОЗДАНИЕ ЧАТОВ ==========

    @Query(value = "SELECT success, error_text, chat_id " +
            "FROM create_personal_chat(:user1Id, :user2Id)", nativeQuery = true)
    ChatCreationResult createPersonalChat(@Param("user1Id") Long user1Id, @Param("user2Id") Long user2Id);

    @Query(value = "SELECT success, error_text, chat_id " +
            "FROM create_group_chat(:chatName, :createdBy, :initialMembers)", nativeQuery = true)
    ChatCreationResult createGroupChat(@Param("chatName") String chatName, @Param("createdBy") Long createdBy, @Param("initialMembers") Long[] initialMembers);

    // ========== УПРАВЛЕНИЕ ЧАТАМИ ==========
    @Query(value = "SELECT success, error_text FROM add_group_member(:chatId, :inviterId, :newUserId)", nativeQuery = true)
    IDBResult addGroupMember(@Param("chatId") Long chatId, @Param("inviterId") Long inviterId, @Param("newUserId") Long newUserId);

    @Query(value = "SELECT success, error_text FROM leave_chat(:chatId, :userId)", nativeQuery = true)
    IDBResult leaveChat(@Param("chatId") Long chatId, @Param("userId") Long userId);

    // ========== ПРОВЕРКИ ПРАВ ==========
    @Query(value = "SELECT EXISTS (SELECT 1 FROM chat_members cm " +
        "WHERE cm.chat_id = :chatId AND cm.user_id = :userId AND cm.is_deleted = FALSE) as is_member", nativeQuery = true)
    Boolean isChatMember(@Param("chatId") Long chatId, @Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO message_read_status (message_id, user_id, read_at) " +
            "VALUES (:messageId, :userId, NOW()) " +
            "ON CONFLICT (message_id, user_id) DO NOTHING", nativeQuery = true)
    void markMessageAsRead(@Param("messageId") Long messageId, @Param("userId") Long userId);

    @Query(value = "SELECT c.is_group FROM chats c WHERE c.id = :chatId", nativeQuery = true)
    Boolean isGroupChat(@Param("chatId") Long chatId);

    // ========== ДОПОЛНИТЕЛЬНЫЕ МЕТОДЫ ==========


    @Query(value = "SELECT cm.is_admin FROM chat_members cm " +
            "WHERE cm.chat_id = :chatId AND cm.user_id = :userId AND cm.is_deleted = FALSE", nativeQuery = true)
    Boolean isChatAdmin(@Param("chatId") Long chatId, @Param("userId") Long userId);
    @Query(value = "SELECT COUNT(*) FROM messages m " +
            "WHERE m.chat_id = :chatId AND m.is_deleted = FALSE AND " +
            "m.id NOT IN (SELECT message_id FROM user_hidden_messages WHERE user_id = :userId)", nativeQuery = true)
    Integer getVisibleMessagesCount(@Param("chatId") Long chatId, @Param("userId") Long userId);
}