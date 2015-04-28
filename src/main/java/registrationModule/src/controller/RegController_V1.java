package registrationModule.src.controller;


import CommunicationModule.src.api.ICommController;
import DatabaseModule.src.api.IDbController;

import registrationModule.src.api.IRegController;
import registrationModule.src.api.IRegRequest_model;
import registrationModule.src.api.IRegVerify_model;

import registrationModule.src.utilities.ModelsHolder;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.dd
 */
public class RegController_V1 implements IRegController {
    private IRegRequest_model registrator = null;
    private IRegVerify_model verification = null;
    private IDbController dbController = null;
    private ICommController commController = null;

    public RegController_V1(){
        ModelsHolder models = new ModelsHolder();
        commController = models.determineCommControllerVersion();
        dbController = models.determineDbControllerVersion();
        registrator = models.determineRegRequestVersion();
        verification = models.determineRegVerifyVersion();
    }

    public void getRegDetails(HashMap<String,String> request) {
        //generate data to send
        HashMap<Integer,HashMap<String,String>> dataToSend = new HashMap<Integer,HashMap<String,String>>();
        HashMap<String,String> data = registrator.regDetailsRequest(request);
        String regID = data.get("RegID");
        ArrayList<String> sendTo = null;
        if (null != regID){
            sendTo = new ArrayList<String>();
            sendTo.add(regID);
        }
        dataToSend.put(1,data);
        //determine how to send the data
        commController.setCommToUsers(dataToSend,sendTo);

        //send the data
        commController.SendResponse();
    }

    public void handleReg(HashMap<String, String> filledForm) {
        HashMap<Integer,HashMap<String,String>> dataToSend = new HashMap<Integer,HashMap<String,String>>();
        //if the user exists (registration model decides how to determine that)
        if(registrator.doesUserExist(filledForm)){
            //add errormessage and response coode to the data to be sent back
            dataToSend.get(1).put("ErrorMessage","An active user with this mail already exists");
            //(what code is it?)
            dataToSend.get(1).put("ResponseCode","XXXXXXXX");
        }
        //User does not exist
        else {
            //Add the new community member (a new CmID is generated)
            String newCmid = Integer.toString(dbController.addNewCommunityMember(filledForm));
            //Separate according to user type and handle accordingly.... use the Verification Module for verification processes
            VerifyDetail(filledForm, newCmid);
        }
        //determine how to send the data
        commController.setCommToUsers(dataToSend);
        //send the data
        commController.SendResponse();
    }

    //public void IVerify(int userType){verification.IVerify(userType);}
    public boolean VerifyDetail(int cmid){
        return verification.VerifyDetail(cmid);
    }

    public String resendMail(String mail,int cmid)
    {
        return verification.resendMail(mail,cmid);
    }
    public void proccesOfOkMember(int cmid)
    {
        verification.proccesOfOkMember(cmid);
    }
}
