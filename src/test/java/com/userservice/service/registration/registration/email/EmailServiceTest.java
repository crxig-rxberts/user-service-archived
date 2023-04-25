package com.userservice.service.registration.registration.email;

import com.userservice.service.registration.email.EmailService;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    private final String sendTo = "test@example.com";

    private final String emailContent = "This is a test email.";


    @Test
    void sendEmailSuccess() {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        emailService.send(sendTo, emailContent);

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }

    @Test
    void sendEmailFailure() throws MailSendException {
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(MailSendException.class).when(mailSender).send(any(MimeMessage.class));

        assertThrows(MailSendException.class, () -> emailService.send(sendTo, emailContent));

        verify(mailSender, times(1)).send(any(MimeMessage.class));
    }
}
