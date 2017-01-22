package com.logicalpanda.geoshare.other;

import com.logicalpanda.geoshare.pojos.User;

/**
 * Created by Ger on 16/01/2017.
 */

public class Globals {
    private static Globals _instance = null;

    private Globals() {
    }

    public static Globals instance() {
        if (_instance == null) {
            _instance = new Globals();
        }
        return _instance;
    }

    private User currentUser = new User();

    public static User getCurrentUser()
    {
        return instance().currentUser;
    }
    public static void setCurrentUser(User user)
    {
        instance().currentUser = user;
    }

}