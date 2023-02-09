package com.userservice.user;

import com.sun.istack.NotNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class UserRequest {

    @NotNull
    private String email;

    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least 8 characters, including one lowercase letter, one uppercase letter, one number, and one special character")
    private String password;

    @Size(min = 4, max = 12)
    private String displayName;

    @Email
    private String newEmail;

    @Size(min = 2, max = 35)
    private String firstName;

    @Size(min = 2, max = 35)
    private String lastName;
    private boolean locked;

    public boolean getLocked() {
        return locked;
    }
}
