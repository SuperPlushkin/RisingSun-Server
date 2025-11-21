package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class ChatCreationOperationResult extends ServiceResult {
    private Long chatId;

    public ChatCreationOperationResult(boolean success, String errorMessage, Long chatId) {
        super(success, errorMessage);
        this.chatId = chatId;
    }
}
