package com.Sunrise.DTO.DBResults;

public interface ChatStatsResult {
    Integer getTotalMessages();
    Integer getDeletedForAll();
    Integer getHiddenByUser();
    Boolean getCanClearForAll();
}
