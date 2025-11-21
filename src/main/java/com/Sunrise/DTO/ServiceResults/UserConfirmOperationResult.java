package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class UserConfirmOperationResult extends ServiceResult {
    private final String jwtToken;
    public UserConfirmOperationResult(boolean success, String errorMessage, String jwtToken){
        super(success, errorMessage);

        this.jwtToken = jwtToken;
    }
}
