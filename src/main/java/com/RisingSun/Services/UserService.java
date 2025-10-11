package com.RisingSun.Services;

import com.RisingSun.DTO.UserDTO;
import com.RisingSun.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDTO> getFilteredUsers(int limited, String filter) {
        Pageable pageable = PageRequest.of(0, limited);
        return userRepository.findFilteredUsers(filter, pageable);
    }
}
