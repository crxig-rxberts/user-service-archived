package com.userservice.registration;

import com.sun.istack.NotNull;
import lombok.*;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class RegistrationRequest {
    @NotNull
    private final String firstName;
    @NotNull
    private final String lastName;
    @NotNull
    private final String displayName;
    @NotNull
    private final String password;
    @NotNull
    private final String email;
}
