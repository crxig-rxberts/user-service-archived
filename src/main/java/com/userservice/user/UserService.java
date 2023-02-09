package com.userservice.user;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public void updateUserCredentials(UserRequest request) {

        UserEntity entity = userRepository.findByEmail(request.getEmail()).orElseThrow(() ->
                new EntityNotFoundException("Entity not found with email: " + request.getEmail()));

        if (request.getPassword() != null) entity.setEmail(bCryptPasswordEncoder.encode(request.getPassword()));
        if (request.getDisplayName() != null) entity.setDisplayName(request.getDisplayName());
        if (request.getNewEmail() != null) entity.setEmail(request.getNewEmail());
        if (request.getFirstName() != null) entity.setFirstName(request.getFirstName());
        if (request.getLastName() != null) entity.setLastName(request.getLastName());
        if (request.getLocked()) entity.setLocked(true);

        userRepository.save(entity);


    }
}
