package com.userservice.registration;

import com.userservice.registration.token.ConfirmationToken;
import com.userservice.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "api/v1/registration")
@AllArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    private final ConfirmationTokenService confirmationTokenService;

    @PostMapping
    public ConfirmationToken register(@RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path = "confirm")
    public ResponseEntity<String> confirm(@RequestParam("token") String token) { return confirmationTokenService.confirmToken(token); }
}
