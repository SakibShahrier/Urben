package com.mobileapplication.urben;

import android.widget.Toast;

public class RequiredDataChecker {

    public boolean providedSignUpDataProperly(String userName, String email, String userType){

        if(userName.equals("") || email.equals("") || userType.equals("")){
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

    public boolean sharedDataAvailable(String userName, String email, String userType){

        if(userName.equals("") || email.equals("") || userType.equals("")){
            return false;
        }
        return true;
    }

    public boolean agentDataAvailable(String counterName, String lat, String lon){
        if(counterName.equals("") || lat.equals("") ||  lon.equals("")){
            return false;
        }
        return true;
    }

}
