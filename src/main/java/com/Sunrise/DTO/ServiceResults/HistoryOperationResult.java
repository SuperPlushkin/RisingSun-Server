package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class HistoryOperationResult extends ServiceResult {
    private final Integer affectedMessages;

    public HistoryOperationResult(boolean success, String errorMessage, Integer affectedMessages) {
        super(success, errorMessage);
        this.affectedMessages = affectedMessages;
    }
}