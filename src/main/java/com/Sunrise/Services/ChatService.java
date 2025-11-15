package com.Sunrise.Services;

import com.Sunrise.Controllers.ChatController;
import com.Sunrise.DTO.DBResults.*;
import com.Sunrise.DTO.ServiceResults.*;
import com.Sunrise.Repositories.ChatRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    @Transactional
    public HistoryOperationResult clearChatHistory(Long chatId, ChatController.ClearType clearType, Long userId) {

        ClearHistoryResult result = switch (clearType) {
            case FOR_ALL -> chatRepository.clearChatHistoryForAll(chatId, userId);
            case FOR_SELF -> chatRepository.clearChatHistoryForSelf(chatId, userId);
        };

        String infoMessage = result.getSuccess()
            ? "Chat history cleared successfully"
            : result.getErrorText();

        return new HistoryOperationResult(result.getSuccess(), infoMessage, result.getAffectedCount());
    }

    @Transactional
    public HistoryOperationResult restoreChatHistory(Long chatId, Long userId) {

        ClearHistoryResult result = chatRepository.restoreChatHistoryForSelf(chatId, userId);

        String infoMessage = result.getSuccess()
            ? "Chat history restored successfully"
            : result.getErrorText();

        return new HistoryOperationResult(result.getSuccess(), infoMessage, result.getAffectedCount());
    }

    @Transactional
    public ChatCreationOperationResult createPersonalChat(Long userId, Long otherUserId) {

        ChatCreationResult result = chatRepository.createPersonalChat(userId, otherUserId);

        String infoMessage = result.getSuccess()
            ? "Personal chat created successfully"
            : result.getErrorText();

        return new ChatCreationOperationResult(result.getSuccess(), infoMessage, result.getChatId());
    }

    @Transactional
    public ChatCreationOperationResult createGroupChat(String chatName, Long createdBy, List<Long> memberIds) {

        Long[] memberIdsArray = memberIds.toArray(new Long[0]);
        ChatCreationResult result = chatRepository.createGroupChat(chatName, createdBy, memberIdsArray);

        String infoMessage = result.getSuccess()
            ? "Group chat created successfully"
            : result.getErrorText();

        return new ChatCreationOperationResult(result.getSuccess(), infoMessage, result.getChatId());
    }

    @Transactional
    public SimpleResult addGroupMember(Long chatId, Long inviterId, Long newUserId) {

        IDBResult result = chatRepository.addGroupMember(chatId, inviterId, newUserId);

        String infoMessage = result.getSuccess()
            ? "User added to group successfully"
            : result.getErrorText();

        return new SimpleResult(result.getSuccess(), infoMessage);
    }

    @Transactional
    public SimpleResult leaveChat(Long chatId, Long userId) {

        IDBResult result = chatRepository.leaveChat(chatId, userId);

        String infoMessage = result.getSuccess()
            ? "Left chat successfully"
            : result.getErrorText();

        return new SimpleResult(result.getSuccess(), infoMessage);
    }

    @Transactional(readOnly = true)
    public ChatStatsOperationResult getChatStats(Long chatId, Long userId) {

        ChatStatsResult result = chatRepository.getChatClearStats(chatId, userId);

        return new ChatStatsOperationResult(
            result.getTotalMessages(),
            result.getDeletedForAll(),
            result.getHiddenByUser(),
            result.getCanClearForAll()
        );
    }

    @Transactional(readOnly = true)
    public List<MessageResult> getChatMessages(Long chatId, Long userId, Integer limit, Integer offset) {
        return chatRepository.getChatMessages(chatId, userId, limit, offset);
    }

    @Transactional(readOnly = true)
    public Boolean isChatMember(Long chatId, Long userId) {
        return chatRepository.isChatMember(chatId, userId);
    }

    @Transactional(readOnly = true)
    public Boolean isChatAdmin(Long chatId, Long userId) {
        return chatRepository.isChatAdmin(chatId, userId);
    }

    @Transactional(readOnly = true)
    public Boolean isGroupChat(Long chatId) {
        return chatRepository.isGroupChat(chatId);
    }

    @Transactional
    public void markMessageAsRead(Long messageId, Long userId) {
        chatRepository.markMessageAsRead(messageId, userId);
    }

    @Transactional(readOnly = true)
    public Integer getVisibleMessagesCount(Long chatId, Long userId) {
        return chatRepository.getVisibleMessagesCount(chatId, userId);
    }
}