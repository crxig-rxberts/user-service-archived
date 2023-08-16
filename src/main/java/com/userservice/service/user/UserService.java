package com.userservice.service.user;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.UnauthorizedException;
import com.userservice.model.request.LoginRequest;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.request.RefreshRequest;
import com.userservice.model.request.UserRequest;
import com.userservice.model.response.LoginResponse;
import com.userservice.model.response.RefreshResponse;
import com.userservice.model.response.UserResponse;
import com.userservice.repository.UserRepository;
import com.userservice.security.jwt.JwtTokenUtil;
import com.userservice.model.response.mapper.ResponseMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private JwtTokenUtil jwtTokenUtil;
    private ResponseMapper responseMapper;

    public ResponseEntity<UserResponse> returnUser(String email) {
        return responseMapper.buildUserResponse(getUserOrElseThrow(email));
    }

    public ResponseEntity<UserResponse> updateUserCredentials(UserRequest request) throws NotFoundException {
        UserEntity userEntity = updateEntity(getUserOrElseThrow(request.getEmail()), request);
        return responseMapper.buildUserResponse(userEntity);
    }

    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        getUserOrElseThrow(loginRequest.getEmail());
        UserDetails userDetails = userRepository.findByEmail(loginRequest.getEmail());
        if (!bCryptPasswordEncoder.matches(loginRequest.getPassword(), userDetails.getPassword())) {
            throw new UnauthorizedException("Passwords do not match.");
        }

        return ResponseEntity.ok(new LoginResponse(jwtTokenUtil.generateToken(userDetails), "refreshToken"));
    }

//    public ResponseEntity<RefreshResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
//
//    }

    private UserEntity updateEntity(UserEntity userEntity, UserRequest request) {
        if (request.getPassword() != null) {
            userEntity.setPassword(bCryptPasswordEncoder.encode(request.getPassword()));
        }
        if (request.getDisplayName() != null) {
            userEntity.setDisplayName(request.getDisplayName());
        }
        if (request.getNewEmail() != null) {
            userEntity.setEmail(request.getNewEmail());
        }
        if (request.getFirstName() != null) {
            userEntity.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            userEntity.setLastName(request.getLastName());
        }
        if (request.getLocked()) {
            userEntity.setLocked(true);
        }
        return userRepository.save(userEntity);
    }

    private UserEntity getUserOrElseThrow(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() ->
                new NotFoundException("No user exists in DB with given credentials."));

    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("No user exists in DB with given credentials."));
    }
}