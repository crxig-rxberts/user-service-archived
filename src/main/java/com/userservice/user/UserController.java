package com.userservice.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/user")
@AllArgsConstructor
public class UserController {

    @GetMapping
    public AppUser getAppUser(@RequestParam String email) { return AppUserService.getUserByEmail(email); }

    // TODO: create endpoint just for login, implement logic in this project to return error messages to client
}
