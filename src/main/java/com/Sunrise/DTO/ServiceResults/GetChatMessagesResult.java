package com.Sunrise.DTO.ServiceResults;

import com.Sunrise.DTO.DBResults.MessageResult;

import java.util.List;

@lombok.Getter
public class GetChatMessagesResult extends ServiceResult {
    private final List<MessageResult> messages;

    public GetChatMessagesResult(boolean success, String errorMessage, List<MessageResult> messages) {
        super(success, errorMessage);

        this.messages = messages;
    }
}
