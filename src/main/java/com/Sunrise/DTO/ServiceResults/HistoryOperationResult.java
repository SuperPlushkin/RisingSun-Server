package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class HistoryOperationResult extends ServiceResult {
    private final Integer affectedMessages;

    public HistoryOperationResult(boolean success, String infoMessage, Integer affectedMessages) {
        super(success, infoMessage);
        this.affectedMessages = affectedMessages;
    }
}