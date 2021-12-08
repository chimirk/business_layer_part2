package com.main;

import com.useradm.UserAdministration;
import com.usermanagementlayer.UserManagementImpl;

public class Main {
    public static void main(String[] args) {
        UserAdministration userAdministration = new UserAdministration(new UserManagementImpl());
        try {
            userAdministration.signUp("mike", "mike lopez", "chirca.mircea@gmail.com" );
        } catch (Exception e) {
            e.printStackTrace();
        }    }
}
