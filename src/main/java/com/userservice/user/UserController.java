package com.userservice.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping(path = "api/v1/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserRepository userRepository;

    private UserService userService;

    @GetMapping
    public Optional<UserEntity> getUserEntity(@RequestParam String email) { return userRepository.findByEmail(email); }

    @PutMapping
    public ResponseEntity<String> update(@RequestBody UserRequest request) {
        userService.updateUserCredentials(request);
        return ResponseEntity.ok("Record updated successfully.");
    }
}
