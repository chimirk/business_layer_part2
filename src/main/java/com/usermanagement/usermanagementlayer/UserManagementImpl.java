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
    public void forgotPassword(String username) {
        UUID changePasswordToken = UUID.randomUUID();
        /*
        The user enters his email. we verify the email if it is registered in the system and it is active, then we send an email with the forgot password token (link) and 
        diactivate the account.
        When the user clicks on the link he/she is redirected to the new password form. He enters a new password and submits it. The system verifies if it is a different password
        than the old one. if it is ok, then we save the new password in the database table and reactivates the user's account.
        
        hint: use the emailVerification method to update the password .
        */
    }

    @Override
    public boolean emailVerification(String token, String password) {
        //Use the token to get the user and then update the password field of that user
        //Once the database has been updated, delete the token
        if (password != null && username != null) {
            UserGateway.savePassword(password, username);
            UserGateway.updateActivationStatus("Y", username);
            return true;
        }

        return false;
    }

    @Override
    public void changePassword(String username, String oldPassword, String newPassword) {
        /*
        Precondition: the user must be logged in.
        
        the user navigates to his profile and requests to change password. He is then redirect to the change password page. He enters the old password and the new password. he 
        submits it and then, the system will verify if the old password is the right passord of the current user and if the new and the old are different passwords. once it has been
        verified, the system will update database with the new password of the user.
        */

    }
}
