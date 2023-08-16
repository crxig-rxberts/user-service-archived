package com.userservice.model.response;

import com.userservice.model.entity.UserEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;

@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Component
public class UserResponse extends BaseResponse {

    private UserEntity userEntity;
}