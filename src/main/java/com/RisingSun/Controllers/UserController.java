package com.RisingSun.Controllers;

import com.RisingSun.DTO.UserDTO;
import com.RisingSun.DTO.UserFilterRequest;
import com.RisingSun.Services.UserService;
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
