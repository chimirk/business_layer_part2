package com.useradm;

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

    public void emailTheVerificationForSignUp(String username, String password) {
        userManagement.emailVerification(username, password, true);
    }

    public void emailTheVerificationForForgotPassword(String username, String password) {
        userManagement.emailVerification(username, password, false);
    }
    public void changePassword(String username, String oldPassword, String newPassword) throws Exception {
        userManagement.changePassword(username, oldPassword, newPassword);
    }
}
