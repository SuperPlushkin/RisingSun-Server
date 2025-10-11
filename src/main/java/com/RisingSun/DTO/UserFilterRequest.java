package com.RisingSun.DTO;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UserFilterRequest {

    @NotNull(message = "limited is required")
    @Min(value = 1, message = "limited must be at least 1")
    @Max(value = 50, message = "limited must be at most 50")
    private Integer limited;

    private String filter;

    public Integer getLimited() { return limited; }
    public void setLimited(Integer limited) { this.limited = limited; }

    public String getFilter() { return filter != null ? filter : ""; }
    public void setFilter(String filter) { this.filter = filter; }
}
