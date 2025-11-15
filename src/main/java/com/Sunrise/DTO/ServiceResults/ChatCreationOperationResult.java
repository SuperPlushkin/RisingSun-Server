package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class ChatCreationOperationResult extends ServiceResult {
    private Long chatId;

    public ChatCreationOperationResult(boolean success, String infoMessage, Long chatId) {
        super(success, infoMessage);
        this.chatId = chatId;
    }
}
