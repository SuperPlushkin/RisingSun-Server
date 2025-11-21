package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class IsChatAdminResult extends ServiceResult {
    private final Boolean isChatAdmin;

    public IsChatAdminResult(boolean success, String errorMessage, Boolean isChatAdmin) {
        super(success, errorMessage);

        this.isChatAdmin = isChatAdmin;
    }
}
