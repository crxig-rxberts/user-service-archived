package com.userservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
public class UpdateEmailRequest {

    private String email;
    private String password;
    @NotNull
    @Email
    private String newEmail;
}
