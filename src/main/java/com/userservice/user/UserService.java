package com.userservice.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void updateUserCredentials(UserRequest request) {

        if(request.getPassword() != null) userRepository.updatePassword(request.getEmail(), bCryptPasswordEncoder.encode(request.getPassword()));
        if(request.getDisplayName() != null) userRepository.updateDisplayName(request.getEmail(), request.getDisplayName());
        if(request.getNewEmail() != null) userRepository.updateEmail(request.getEmail(), request.getNewEmail());
        if(request.getFirstName() != null) userRepository.updateFirstName(request.getEmail(), request.getFirstName());
        if(request.getLastName() != null) userRepository.updateLastName(request.getEmail(), request.getLastName());
        if(request.getLocked() != null) userRepository.lockAccount(request.getEmail(), request.getLocked());

        log.info("User credentials modified on DB. Correlation Id: " + MDC.get("x-correlation-id"));


    }
}
