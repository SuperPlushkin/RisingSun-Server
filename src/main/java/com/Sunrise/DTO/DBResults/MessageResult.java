package com.Sunrise.DTO.DBResults;

public interface MessageResult {
    Long getMessageId();
    Long getSenderId();
    String getSenderUsername();
    String getText();
    String getSentAt();
    Boolean getIsDeleted();
    Long getReadCount();
    Boolean getIsHiddenByUser();
}