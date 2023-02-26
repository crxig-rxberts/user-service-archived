package com.userservice.service.registration.email;

public interface EmailSender {
    void send(String to, String email);
}
