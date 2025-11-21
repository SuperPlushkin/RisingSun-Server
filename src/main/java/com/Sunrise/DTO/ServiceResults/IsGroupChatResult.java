package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class IsGroupChatResult extends ServiceResult {
    private final Boolean isGroupChat;

    public IsGroupChatResult(boolean success, String errorMessage, Boolean isGroupChat) {
        super(success, errorMessage);

        this.isGroupChat = isGroupChat;
    }
}
