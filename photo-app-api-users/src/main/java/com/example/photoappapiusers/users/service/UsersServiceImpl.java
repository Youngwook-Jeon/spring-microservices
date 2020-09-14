package com.example.photoappapiusers.users.service;

import com.example.photoappapiusers.users.shared.UserDto;

import java.util.UUID;

public class UsersServiceImpl implements UsersService {

    @Override
    public UserDto createUser(UserDto userDto) {
        userDto.setUserId(UUID.randomUUID().toString());
        return null;
    }
}
