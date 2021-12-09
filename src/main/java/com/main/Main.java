package com.main;

import com.useradm.UserAdministration;
import com.usermanagementlayer.UserManagementImpl;

public class Main {
    public static void main(String[] args) throws Exception {
        UserAdministration userAdministration = new UserAdministration(new UserManagementImpl());
        /*try {
            userAdministration.signUp("mike", "mike lopez", "chirca.mircea@gmail.com" );
        } catch (Exception e) {
            e.printStackTrace();
        }  */
        userAdministration.changePassword("user1", "123", "1234");
    }
}
