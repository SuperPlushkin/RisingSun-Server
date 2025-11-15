package com.Sunrise.DTO.ServiceResults;

public record ChatStatsOperationResult(int totalMessages, int deletedForAll, int hiddenByUser, boolean canClearForAll) { }