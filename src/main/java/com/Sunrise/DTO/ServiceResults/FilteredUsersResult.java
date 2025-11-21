package com.Sunrise.DTO.ServiceResults;

import java.util.List;

@lombok.Getter
public class FilteredUsersResult extends ServiceResult{
    private final List<UserDTO> users;

    public FilteredUsersResult(boolean success, String errorMessage, List<UserDTO> users){
        super(success, errorMessage);

        this.users = users;
    }
}
