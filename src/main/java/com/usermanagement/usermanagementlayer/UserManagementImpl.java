package com.usermanagement.usermanagementlayer;

import com.usermanagement.gateways.emailgateway.emailGateway;
import com.usermanagement.useradmin.UserManagement;

import javax.mail.MessagingException;
import java.util.UUID;

public class UserManagementImpl implements UserManagement {
    @Override
    public void signUp(String username, String fullName, String email) {
        UUID token = UUID.randomUUID();

        //save the token to the sql database
        //VerificationTokensGateway.saveToken(token, username);

        //save user info
        //UserGateway.saveUser(username, email, fullName);

        //send verification email to user
        try {
            emailGateway.sendVerification(email,token);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forgotPassword() {

    }

    @Override
    public boolean emailVerification() {
        return false;
    }

    @Override
    public void changePassword() {

    }
}
