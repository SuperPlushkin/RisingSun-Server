package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
public class UserInsertOperationResult extends ServiceResult {
    public String token;
    public UserInsertOperationResult(boolean success, String info_message, String token){
        super(success, info_message);

        this.token = token;
    }
}