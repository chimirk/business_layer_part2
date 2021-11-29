package com.usermanagement.useradmin;

public interface UserManagement {
    void signUp( String username, String fullName, String email);
    void forgotPassword(String username, String email);
    boolean emailVerification(String username, String password);
    void changePassword();
}
