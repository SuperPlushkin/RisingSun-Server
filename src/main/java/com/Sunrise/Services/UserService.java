package com.Sunrise.Services;

import com.Sunrise.DTO.ServiceResults.UserDTO;
import com.Sunrise.Repositories.UserRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    public List<UserDTO> getFilteredUsers(int limited, int offset, String filter) {
        Pageable pageable = PageRequest.of(offset, limited);
        return userRepository.findFilteredUsers(filter, pageable);
    }
}
