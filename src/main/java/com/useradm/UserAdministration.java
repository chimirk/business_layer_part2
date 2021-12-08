package com.useradm;

import com.usermanagementlayer.UserManagementException;
import com.usermanagementlayerinterface.UserManagement;

public class UserAdministration {
    private UserManagement userManagement;
    //private UserManagement userManagement;

    public UserAdministration(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    public void signUp(String username, String fullName, String email) throws Exception {
        userManagement.signUp(username, fullName, email);
    }

    public void forgotPassword(String username) throws Exception {

        userManagement.forgotPassword(username);
    }

    public void emailVerificationForSignUp(String username, String password) throws UserManagementException {
        userManagement.emailVerification(username, password, true);
    }

    public void emailVerificationForForgotPassword(String username, String password) throws UserManagementException {
        userManagement.emailVerification(username, password, false);
    }
    public void changePassword(String username, String oldPassword, String newPassword) throws Exception {
        userManagement.changePassword(username, oldPassword, newPassword);
    }
}