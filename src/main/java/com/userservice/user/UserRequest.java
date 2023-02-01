package com.userservice.user;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserRequest {

    private String email;
    private String password;
    private String displayName;
    private String newEmail;
    private String firstName;
    private String lastName;
    private Boolean locked;


}
