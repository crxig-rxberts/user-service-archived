package com.userservice.model.response.mapper;

import com.userservice.model.entity.UserEntity;
import com.userservice.model.response.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ResponseMapper {

    public ResponseEntity<UserResponse> buildUserResponse(UserEntity userEntity) {
        UserResponse response = new UserResponse();
        response.setStatus(ResponseStatus.SUCCESS);
        response.setUserEntity(userEntity);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

