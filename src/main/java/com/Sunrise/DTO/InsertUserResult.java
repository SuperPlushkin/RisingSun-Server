package com.Sunrise.DTO;

public interface InsertUserResult {
    Boolean getSuccess();
    String getErrorText();
    String getGeneratedToken();
}