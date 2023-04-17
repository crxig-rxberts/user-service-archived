package com.userservice.service.user;

import com.userservice.exception.NotFoundException;
import com.userservice.service.response.Response;
import com.userservice.service.response.ResponseMapper;
import com.userservice.service.response.ResponseMapperEnum;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/v1/user")
@AllArgsConstructor
@Slf4j
public class UserController {

    private UserRepository userRepository;
    private UserService userService;
    private ResponseMapper responseMapper;

    // TODO: use service classes in controller
    @GetMapping
    public ResponseEntity<Response> getUserEntity(@RequestParam String email) throws NotFoundException {
        return checkUserExistsAndReturnUser(email);
    }

    @PutMapping
    public ResponseEntity<Response> updateUserCredentials(@RequestBody UserRequest request) {
        return userService.updateUserCredentials(request);
    }

    private ResponseEntity<Response> checkUserExistsAndReturnUser(String email) {
        try {
            UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(() ->
                    new NotFoundException("No user exists in DB with given credentials. Correlation Id: " + MDC.get("x-correlation-id")));
            return responseMapper.buildResponse(userEntity);
        }
        catch (NotFoundException ex) {
            return responseMapper.buildResponse(ex);
        }
    }
}
