package com.main;

import com.usermanagement.useradmin.UserAdministration;
import com.usermanagement.usermanagementlayer.UserManagementImpl;

public class Main {
    public static void main(String[] args) {
        UserAdministration userAdministration = new UserAdministration(new UserManagementImpl());
        userAdministration.signUpUser("mike", "mike lopez", "chirca.mircea@gmail.com" );
    }
}
