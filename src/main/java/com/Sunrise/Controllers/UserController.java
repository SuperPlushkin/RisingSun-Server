package com.Sunrise.Controllers;

import com.Sunrise.DTO.UserDTO;
import com.Sunrise.DTO.UserFilterRequest;
import com.Sunrise.Services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/getmany")
    public List<UserDTO> getManyUsers(@Valid @RequestBody UserFilterRequest request) {
        return userService.getFilteredUsers(request.getLimited(), request.getOffset(), request.getFilter());
    }
}
