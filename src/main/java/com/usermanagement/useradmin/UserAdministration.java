package com.usermanagement.useradmin;

public class UserAdministration {
    private UserManagement userManagement;

    public UserAdministration(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    public void signUpUser(String username, String fullName, String email) {
        userManagement.signUp(username, fullName, email);
    }

    public void forgotPasswordUser(String username, String email) {

        userManagement.forgotPassword(username, email);
    }

    public void emailVerificationUser(String username, String password) {
        userManagement.emailVerification(username, password);
    }
    public void changePasswordUser() {
        userManagement.changePassword();
    }
}
