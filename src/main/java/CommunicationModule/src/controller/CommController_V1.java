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
    private final int commOfficialVersion = 1;

    //holding the implementations chosen for the interface (composition)
    private ICommToUsers_model commToUsers = null;
    private ICommOfficial_model commOfficial = null;

    public Object sendResponse() {
        if(null != commToUsers) {
            return commToUsers.sendResponse();
        }
        else{
            //throw some kind of alert?
            return null;
        }
    }

    public void sendMessage()  {
        if (null != commOfficial) {
            commOfficial.sendMessage();
        }
        else{
            //throw some kind of alert?
        }
    }

    //set methods for the members
    public void setCommOfficial(HashMap<String,String> data,int type){
        ICommOfficialFactory commOfficialFact = determineCommOfficialVersion();
        commOfficial = commOfficialFact.createComm(data,type);
    }
    public void setCommToUsers(HashMap<Integer, HashMap<String, String>> data,
                               ArrayList<String> target,boolean initiatedComm){
        ICommToUsersFactory commToUsersFact = determineCommToUsersVersion();
        commToUsers = commToUsersFact.createComm(data,target,initiatedComm);
    }


    private ICommToUsersFactory determineCommToUsersVersion(){
        switch (commToUsersVersion) {
            //determine version of CommToUsers
            case 1: {
                return new CommToUsersFactory_V1();
            }
            default: {
                return null;
            }
        }
    }

    private ICommOfficialFactory determineCommOfficialVersion(){
        switch (commOfficialVersion) {
            //determine version of CommToMail to use
            case 1: {
                return new CommOfficialFactory_V1();
            }
            default: {
                return null;
            }
        }
    }
}
