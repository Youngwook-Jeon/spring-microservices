package com.example.photoappapiusers.users.ui.controllers;

import com.example.photoappapiusers.users.ui.models.CreateUserRequestModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UsersController {

    @Autowired
    private Environment env;

    @GetMapping("/status/check")
    public String status() {
        return "Working on port " + env.getProperty("local.server.port");
    }

    @PostMapping
    public String createUser(@RequestBody CreateUserRequestModel userRequestModel) {
        return "Create user method is called.";
    }
}
