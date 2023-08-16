package com.userservice.service.user;

import com.userservice.exception.NotFoundException;
import com.userservice.exception.UnauthorizedException;
import com.userservice.model.entity.UserEntity;
import com.userservice.model.request.UpdateDetailsRequest;
import com.userservice.model.request.UpdateEmailRequest;
import com.userservice.model.request.UpdatePasswordRequest;
import com.userservice.model.response.BaseResponse;
import com.userservice.model.response.UserResponse;
import com.userservice.model.response.mapper.ResponseStatus;
import com.userservice.repository.UserRepository;
import com.userservice.model.response.mapper.ResponseMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {
    private UserRepository userRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private ResponseMapper responseMapper;

    public ResponseEntity<UserResponse> returnUser(String email) {
        return responseMapper.buildUserResponse(getUserOrElseThrow(email));
    }

    public ResponseEntity<BaseResponse> updatePassword(UpdatePasswordRequest request) throws NotFoundException {
        UserEntity userEntity = getUserOrElseThrow(request.getEmail());

        authenticateGivenPassword(request.getPassword(), userEntity.getPassword());

        userEntity.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        userRepository.save(userEntity);

        return ResponseEntity.ok(new BaseResponse(ResponseStatus.SUCCESS, null));
    }

    public ResponseEntity<BaseResponse> updateEmail(UpdateEmailRequest request) throws NotFoundException {
        UserEntity userEntity = getUserOrElseThrow(request.getEmail());

        authenticateGivenPassword(request.getPassword(), userEntity.getPassword());

        userEntity.setEmail(request.getNewEmail());
        userRepository.save(userEntity);

        return ResponseEntity.ok(new BaseResponse(ResponseStatus.SUCCESS, null));
    }

    public ResponseEntity<BaseResponse> updateDetails(UpdateDetailsRequest request) throws NotFoundException {
        UserEntity userEntity = getUserOrElseThrow(request.getEmail());

        authenticateGivenPassword(request.getPassword(), userEntity.getPassword());

        userRepository.save(updateEntity(userEntity, request));

        return ResponseEntity.ok(new BaseResponse(ResponseStatus.SUCCESS, null));
    }

    private UserEntity updateEntity(UserEntity userEntity, UpdateDetailsRequest request) {
        Optional.ofNullable(request.getDisplayName()).ifPresent(userEntity::setDisplayName);
        Optional.ofNullable(request.getFirstName()).ifPresent(userEntity::setFirstName);
        Optional.ofNullable(request.getLastName()).ifPresent(userEntity::setLastName);
        return userEntity;
    }

    private UserEntity getUserOrElseThrow(String email) {
        return userRepository.findUserByEmail(email).orElseThrow(() ->
                new NotFoundException("No user exists in DB with given credentials."));

    }

    private void authenticateGivenPassword(String givenPassword, String storedPassword) {
        if (!bCryptPasswordEncoder.matches(givenPassword, storedPassword)) {
            throw new UnauthorizedException("Given Password incorrect for User.");
        }
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("No user exists in DB with given credentials."));
    }
}