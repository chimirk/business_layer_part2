package com.usermanagement.gateways.emailgateway;

import javax.mail.MessagingException;
import java.util.Objects;
import java.util.UUID;

public class emailGateway {

    public static void sendVerification(String userEmail, UUID token) throws MessagingException {

        Objects.requireNonNull(EmailVerification.INSTANCE).sendVerificationByEmail(userEmail, token);

    }
}
