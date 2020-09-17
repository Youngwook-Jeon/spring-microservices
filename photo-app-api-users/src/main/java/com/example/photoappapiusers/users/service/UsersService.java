package com.example.photoappapiusers.users.service;

import com.example.photoappapiusers.users.shared.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UsersService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
}
