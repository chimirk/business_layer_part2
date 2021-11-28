package com.usermanagement.useradmin;

public class UserAdministration {
    private UserManagement userManagement;

    public UserAdministration(UserManagement userManagement) {
        this.userManagement = userManagement;
    }

    public void signUpUser(String username, String fullName, String email) {
        userManagement.signUp(username, fullName, email);
    }

    public void forgotPasswordUser() {
        userManagement.forgotPassword();
    }

    public void emailVerificationUser() {
        userManagement.emailVerification();
    }
    public void changePasswordUser() {
        userManagement.changePassword();
    }
}
