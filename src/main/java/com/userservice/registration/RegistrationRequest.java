package com.userservice.registration;

import com.sun.istack.NotNull;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class RegistrationRequest {

    @Size(min = 2, max = 35)
    private String firstName;

    @Size(min = 2, max = 35)
    private String lastName;

    @Size(min = 4, max = 12)
    private String displayName;

    @NotNull
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$",
            message = "Password must contain at least 8 characters, including one lowercase letter, one uppercase letter, one number, and one special character")
    private String password;

    @Email
    private String email;
}
