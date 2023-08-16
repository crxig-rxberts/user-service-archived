package com.userservice.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UpdateDetailsRequest {
    private String email;
    private String password;
    private String displayName;
    private String firstName;
    private String lastName;
}
