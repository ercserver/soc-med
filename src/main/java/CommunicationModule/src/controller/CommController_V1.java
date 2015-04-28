package CommunicationModule.src.controller;

import CommunicationModule.src.api.*;
import CommunicationModule.src.model.*;


import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public class CommController_V1 implements ICommController {
    //version to use - change this to change version - edit decision methos in accordance
    private final int commToUsersVersion = 1;
    private final int commToMailVersion = 1;


    //holding the implementations chosen for the interface (composition)
    private ICommToUsers_model commToUsers = null;
    private ICommToMail_model commToMail = null;

    //Default C'tor
    public CommController_V1() {}



    public void SendResponse() {
        if(null != commToUsers) {
            commToUsers.SendResponse();
        }
        else{
            //throw some kind of alert?
        }
    }


    public void sendEmail()  {
        if (null != commToMail) {
            commToMail.sendEmail();
        }
        else{
            //throw some kind of alert?
        }
    }

    //set methods for the members
    public void setCommToMail(String emailAdress,String emailMessage,
                              String subj){
        commToMail = determineCommToMailVersion(emailAdress,emailMessage,subj);
    }
    public void setCommToUsers(HashMap<Integer, HashMap<String, String>> data,
                               ArrayList<String> target,boolean initiatedComm){
        ICommToUsersFactory commToUsersFact = determineCommToUsersVersion();
        commToUsersFact.createComm(data,target,initiatedComm);
    }


    private ICommToUsersFactory determineCommToUsersVersion(){
        switch (commToUsersVersion) {
            //determine version of CommToUsers to use
            case 1: {
                return new CommToUsersFactory_V1();
            }
            default: {
                return null;
            }
        }
    }

    private ICommToMail_model determineCommToMailVersion(String emailAdress,
                                                         String emailMessage,
                                                         String subject){
        switch (commToMailVersion) {
            //determine version of CommToMail to use
            case 1: {
                return new CommToMail_V1(emailAdress,emailMessage,subject);
            }
            default: {
                return null;
            }
        }
    }
}
