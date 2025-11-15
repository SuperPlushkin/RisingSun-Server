package com.Sunrise.Controllers;

import com.Sunrise.DTO.ServiceResults.UserDTO;
import com.Sunrise.DTO.Requests.UserFilterRequest;
import com.Sunrise.Services.UserService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/app/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/getmany")
    public ResponseEntity<?> getManyUsers(@Valid @ModelAttribute UserFilterRequest request) {

        var users = userService.getFilteredUsers(request.getLimited(), request.getOffset(), request.getFilter());

        return ResponseEntity.ok(Map.of(
            "users", users,
            "count", users.size()
        ));
    }
}
