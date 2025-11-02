package com.Sunrise.DTO.ServiceAndController;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

@lombok.Getter
@lombok.Setter
public class UserFilterRequest {

    @NotNull(message = "limited is required")
    @Min(value = 1, message = "limited must be at least 1")
    @Max(value = 50, message = "limited must be at most 50")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer limited;

    @Min(value = 0, message = "offset must be at least 0")
    @Max(value = Integer.MAX_VALUE, message = "limited must be at most 50")
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer offset = 0;

    private String filter = "";

    public String getFilter() {
        return filter != null ? filter : "";
    }
}
