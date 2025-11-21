package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class VisibleMessagesCountResult extends ServiceResult {
    private final Integer visibleMessagesCount;

    public VisibleMessagesCountResult(boolean success, String errorMessage, Integer visibleMessagesCount) {
        super(success, errorMessage);

        this.visibleMessagesCount = visibleMessagesCount;
    }
}
