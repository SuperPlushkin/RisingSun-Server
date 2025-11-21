package com.Sunrise.DTO.ServiceResults;


@lombok.Getter
public final class ChatStatsOperationResult extends ServiceResult {
    private final Integer totalMessages;
    private final Integer deletedForAll;
    private final Integer hiddenByUser;
    private final Boolean canClearForAll;

    public ChatStatsOperationResult(Boolean success, String errorMessage, Integer totalMessages, Integer deletedForAll, Integer hiddenByUser, Boolean canClearForAll) {
        super(success, errorMessage);

        this.totalMessages = totalMessages;
        this.deletedForAll = deletedForAll;
        this.hiddenByUser = hiddenByUser;
        this.canClearForAll = canClearForAll;
    }
}