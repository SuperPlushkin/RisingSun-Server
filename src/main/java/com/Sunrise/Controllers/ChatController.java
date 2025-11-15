package com.Sunrise.Controllers;

import com.Sunrise.Subclasses.CurrentUserId;
import com.Sunrise.DTO.Requests.AddGroupMemberRequest;
import com.Sunrise.DTO.Requests.CreateGroupChatRequest;
import com.Sunrise.DTO.Requests.CreatePersonalChatRequest;
import com.Sunrise.DTO.ServiceResults.*;
import com.Sunrise.Subclasses.MyException;
import com.Sunrise.Services.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/app/chat")
public class ChatController {

    private final ChatService chatService;
    public enum ClearType { FOR_ALL, FOR_SELF }

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/create-personal")
    public ResponseEntity<?> createPersonalChat(@RequestBody CreatePersonalChatRequest request, @CurrentUserId Long userId) {
        var result = chatService.createPersonalChat(userId, request.getOtherUserId());

        if (result.isSuccess())
        {
            return ResponseEntity.ok(Map.of(
                "message", result.getInfoMessage(),
                "chat_id", result.getChatId()
            ));
        }
        else throw new MyException(result.getInfoMessage());
    }

    @PostMapping("/create-group")
    public ResponseEntity<?> createGroupChat(@RequestBody CreateGroupChatRequest request, @CurrentUserId Long userId) {
        var result = chatService.createGroupChat(request.getChatName(), userId, request.getMemberIds());

        if (result.isSuccess())
        {
            return ResponseEntity.ok(Map.of(
                "message", result.getInfoMessage(),
                "chat_id", result.getChatId()
            ));
        }
        else throw new MyException(result.getInfoMessage());
    }

    @PostMapping("/{chatId}/add-member")
    public ResponseEntity<?> addGroupMember(@PathVariable Long chatId, @RequestBody AddGroupMemberRequest request, @CurrentUserId Long userId) {
        var result = chatService.addGroupMember(chatId, userId, request.getNewUserId());

        if (result.isSuccess())
        {
            return ResponseEntity.ok(result.getInfoMessage());
        }
        else throw new MyException(result.getInfoMessage());
    }

    @PostMapping("/{chatId}/leave")
    public ResponseEntity<?> leaveChat(@PathVariable Long chatId, @CurrentUserId Long userId) {
        var result = chatService.leaveChat(chatId, userId);

        if (result.isSuccess())
        {
            return ResponseEntity.ok(result.getInfoMessage());
        }
        else throw new MyException(result.getInfoMessage());
    }

    @GetMapping("/{chatId}/stats")
    public ResponseEntity<?> getChatStats(@PathVariable Long chatId, @CurrentUserId Long userId) {
        ChatStatsOperationResult result = chatService.getChatStats(chatId, userId);

        return ResponseEntity.ok(Map.of(
            "total_messages", result.totalMessages(),
            "deleted_for_all", result.deletedForAll(),
            "hidden_by_user", result.hiddenByUser(),
            "can_clear_for_all", result.canClearForAll()
        ));
    }

    @PostMapping("/{chatId}/clear-history")
    public ResponseEntity<?> clearChatHistory(@PathVariable Long chatId, @RequestParam(defaultValue = "FOR_SELF") ClearType clearType, @CurrentUserId Long userId) {
        HistoryOperationResult result = chatService.clearChatHistory(chatId, clearType, userId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                "message", result.getInfoMessage(),
                "cleared_messages", result.getAffectedMessages()
            ));
        }
        else throw new MyException(result.getInfoMessage());
    }

    @PostMapping("/{chatId}/restore-history")
    public ResponseEntity<?> restoreChatHistory(@PathVariable Long chatId, @CurrentUserId Long userId) {

        var result = chatService.restoreChatHistory(chatId, userId);

        if (result.isSuccess()) {
            return ResponseEntity.ok(Map.of(
                    "message", result.getInfoMessage(),
                    "restored_messages", result.getAffectedMessages()
            ));
        }
        else throw new MyException(result.getInfoMessage());
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<?> getChatMessages(@PathVariable Long chatId, @RequestParam(defaultValue = "50") Integer limited, @RequestParam(defaultValue = "0") Integer offset, @CurrentUserId Long userId) {
        var messages = chatService.getChatMessages(chatId, userId, limited, offset);

        return ResponseEntity.ok(Map.of(
            "messages", messages,
            "count", messages.size()
        ));
    }

    @PostMapping("/{chatId}/mark-read/{messageId}")
    public ResponseEntity<?> markMessageAsRead(@PathVariable Long chatId, @PathVariable Long messageId, @CurrentUserId Long userId) {

        // Проверяем что пользователь является участником чата
        if (chatService.isChatMember(chatId, userId)) {

            chatService.markMessageAsRead(messageId, userId);
            return ResponseEntity.ok("Successfully marked message as read");
        }
        else throw new MyException("User is not a member of this chat");
    }

    @GetMapping("/{chatId}/message-count")
    public ResponseEntity<?> getVisibleMessageCount(@PathVariable Long chatId, @CurrentUserId Long userId) {
        Integer count = chatService.getVisibleMessagesCount(chatId, userId);

        return ResponseEntity.ok(Map.of("count", count));
    }


    // Чтобы было, по факту бесполезно
    @GetMapping("/{chatId}/is-admin")
    public ResponseEntity<?> isChatAdmin(@PathVariable Long chatId, @CurrentUserId Long userId) {
        Boolean isAdmin = chatService.isChatAdmin(chatId, userId);

        return ResponseEntity.ok(Map.of("is_admin", isAdmin));
    }
    @GetMapping("/{chatId}/is-group")
    public ResponseEntity<?> isGroupChat(@PathVariable Long chatId) {
        Boolean isGroup = chatService.isGroupChat(chatId);

        return ResponseEntity.ok(Map.of("is_group", isGroup));
    }
}
