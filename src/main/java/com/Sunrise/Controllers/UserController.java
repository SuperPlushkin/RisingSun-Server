package com.Sunrise.Controllers;

import com.Sunrise.DTO.ServiceAndController.UserDTO;
import com.Sunrise.DTO.ServiceAndController.UserFilterRequest;
import com.Sunrise.Services.UserService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/app/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/getmany")
    public List<UserDTO> getManyUsers(@Valid @ModelAttribute  UserFilterRequest request) {
        return userService.getFilteredUsers(request.getLimited(), request.getOffset(), request.getFilter());
    }
}
