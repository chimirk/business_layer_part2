package com.usermanagement.useradmin;

public interface UserManagement {
    void signUp( String username, String fullName, String email);
    void forgotPassword();
    boolean emailVerification();
    void changePassword();
}
