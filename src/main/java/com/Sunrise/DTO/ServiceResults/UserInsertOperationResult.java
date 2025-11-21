package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class UserInsertOperationResult extends ServiceResult {
    private final String token;
    public UserInsertOperationResult(boolean success, String errorMessage, String token){
        super(success, errorMessage);

        this.token = token;
    }
}