package com.mobileapplication.urben;

import android.widget.Toast;

public class RequiredDataChecker {

    public boolean providedSignUpDataProperly(String userName, String email, int age, String gender, String password){

        if(userName.equals("") || password.equals("") || email.equals("") || (age == 0) || gender.equals("")){
            return false;
        }
        return true;
    }

    public boolean providedLoginDataProperly(String email, String password){

        if(password.equals("") || email.equals("")){
            return false;
        }
        return true;
    }

    public boolean sharedDataAvailable(String userName, String email, int age, String gender, String password, String userType){

        if(userName.equals("") || password.equals("") || email.equals("") || (age == 0) || gender.equals("") || userType.equals("")){
            return false;
        }
        return true;
    }

}
