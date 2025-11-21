package com.Sunrise.DTO.ServiceResults;

@lombok.Getter
@lombok.AllArgsConstructor
public abstract class ServiceResult {
    private boolean success;
    private String errorMessage;
}
