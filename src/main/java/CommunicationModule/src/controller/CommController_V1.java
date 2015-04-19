package CommunicationModule.src.controller;


import CommunicationModule.src.api.ICommController;
import CommunicationModule.src.api.ICommToMail_model;
import CommunicationModule.src.api.ICommToUsers_model;
import CommunicationModule.src.model.CommToMail_V1;
import CommunicationModule.src.model.CommToUsersFactory_V1;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public class CommController_V1 implements ICommController {
    //holding the implementations chosen for the interface (composition)
    private ICommToUsers_model commToUsers = null;
    private ICommToMail_model commToMail = null;

    //C'tor - use this C'tor just to communicate to mail only
    CommController_V1(String emailAdress,String emailMessage, String sub){
        setCommToMail(emailAdress, emailMessage, sub);
    }
    //C'tor - initialize with the chosen implementations for the interfaces
    //use this C'tor when only communication to users is needed
    CommController_V1(HashMap<Integer,HashMap<String,String>>  data, int userType) {
        setCommToUsers(data, userType);
    }
    //C'tor - initialize with the chosen implementations for the interfaces
    //use this C'tor when both emails and communications to users are needed
    CommController_V1(HashMap<Integer,HashMap<String,String>>  data, int userType,String emailAdress,String emailMessage, String sub) {
        setCommToUsers(data,userType);
        setCommToMail(emailAdress, emailMessage, sub);
    }


    @Override
    public void SendResponse() {
        if(null != commToUsers) {
            commToUsers.SendResponse();
        }
        else{
            //throw some kind of alert?
        }
    }

    @Override
    public void sendEmail()  {
        if (null != commToMail) {
            commToMail.sendEmail();
        }
        else{
            //throw some kind of alert?
        }
    }

    //set methods for the members
    public void setCommToMail(String emailAdress,String emailMessage, String sub){
        commToMail = new CommToMail_V1(emailAdress,emailMessage, sub);
    }
    public void setCommToUsers(HashMap<Integer,HashMap<String,String>>  data, int userType){
        commToUsers = new CommToUsersFactory_V1().createComm(data,userType);
    }
}
