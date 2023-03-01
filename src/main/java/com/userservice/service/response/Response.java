package com.userservice.service.response;

import com.userservice.service.registration.token.ConfirmationToken;
import com.userservice.service.user.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.stereotype.Component;


@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Component
public class Response {

    private ResponseStatus status;
    private String errorMessage;
    private UserEntity userEntity;

}
