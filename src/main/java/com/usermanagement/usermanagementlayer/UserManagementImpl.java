package com.usermanagement.usermanagementlayer;

import com.database.UserGateway;
import com.database.VerificationTokensGateway;
import com.usermanagement.gateways.emailgateway.emailGateway;
import com.usermanagement.useradmin.UserManagement;

import javax.mail.MessagingException;
import java.util.UUID;

public class UserManagementImpl implements UserManagement {

    @Override
    public void signUp(String username, String fullName, String email) {
        UUID verificationToken = UUID.randomUUID();

        //save the token to the sql database
        VerificationTokensGateway.saveToken(verificationToken, username);

        //save user info
        UserGateway.saveUser(username, email, fullName);

        //send verification email to user
        try {
            emailGateway.sendVerification(email,verificationToken);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void forgotPassword(String username, String email) {
        UUID changePasswordToken = UUID.randomUUID();

    }

    @Override
    public boolean emailVerification(String username, String password) {

        if (password != null && username != null) {
            UserGateway.savePassword(password, username);
            UserGateway.updateActivationStatus("Y", username);
            return true;
        }

        return false;
    }

    @Override
    public void changePassword() {

    }
}
