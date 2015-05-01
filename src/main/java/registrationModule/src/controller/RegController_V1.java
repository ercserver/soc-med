package registrationModule.src.controller;

import CommunicationModule.src.api.ICommController;
import DatabaseModule.src.api.IDbController;
import registrationModule.src.api.IRegController;
import registrationModule.src.api.IRegRequest_model;
import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.utilities.ModelsFactory;

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
        ModelsFactory models = new ModelsFactory();
        commController = models.determineCommControllerVersion();
        dbController = models.determineDbControllerVersion();
        registrator = models.determineRegRequestVersion();
        verification = models.determineRegVerifyVersion();
    }

    public Object getRegDetails(HashMap<String,String> request) {
        //generate data to send
        HashMap<Integer,HashMap<String,String>> dataToSend = new HashMap<Integer,HashMap<String,String>>();
        HashMap<String,String> data = registrator.regDetailsRequest(request);
        ArrayList<String> sendTo = sendTo(data);
        dataToSend.put(1, data);
        //determine how to send the data
        commController.setCommToUsers(dataToSend, sendTo, false);

        //send the data
        return commController.sendResponse();
    }

    public Object handleReg(HashMap<String, String> filledForm) {
        HashMap<Integer,HashMap<String,String>> dataToSend = new HashMap<Integer,HashMap<String,String>>();
        //if the user exists (registration model decides how to determine that)
        String message = registrator.doesUserExist(filledForm);
        String responseCode = null;
        if(null != message){
            //What code is it?
            responseCode = "100";
        }
        //User does not exist
        else {
            //Separate according to user type and handle accordingly.... use the Verification Module for verification processes
            ArrayList<String> messages = verification.verifyFilledForm(filledForm);

            if(!messages.isEmpty()){
                //What code is it?
                responseCode = "101";
                message = appendAllMessages(messages);
            }
            //Legit form
            else{
                //What code is it?
                responseCode = "102";
                //Add the new community member (a new CmID is generated)
                int newCmid = dbController.addNewCommunityMember(filledForm);
                //Update status to "Verifying Email"
                dbController.updateStatus(newCmid,null,"'verifyingEmail'");
                //Get authorization method from db
                int authMethod = dbController.getAuthenticationMethod("Israel");
                //Generate data for authorization message
                HashMap<String, String> data = verification.generateDataForAuth(filledForm,authMethod);
                //Create authorization comm
                ICommController commAuthMethod = new ModelsFactory().determineCommControllerVersion();
                commAuthMethod.setCommOfficial(data,authMethod);
                //Communicate authorization (email/sms/...)
                commAuthMethod.sendMessage();
            }

        }
        HashMap<String,String> data = new HashMap<String, String>();
        data.put("Message", message);
        data.put("ResponseCode",responseCode);
        //determine who to send
        ArrayList<String> sendTo = sendTo(filledForm);
        dataToSend.put(1,data);
        //determine how to send the data - initiated communication so use "false"
        commController.setCommToUsers(dataToSend,sendTo,false);
        //send the data
        return commController.sendResponse();
    }

    private String appendAllMessages(ArrayList<String> messages) {
        String message = "An error has occurred!\n";
        int index = 1;
        for(String msg : messages){
            message += Integer.toString(index) + ". " + msg + "\n";
        }
        message += "Please correct the above fields and re-submit the registrations form.";

        return message;
    }

    private ArrayList<String> sendTo(HashMap<String,String> data){
        String regID = data.get("RegID");
        ArrayList<String> sendTo = null;
        if (null != regID){
            sendTo = new ArrayList<String>();
            sendTo.add(regID);
        }
        return sendTo;
    }

    public Object VerifyDetail(int cmid){
        return verification.VerifyDetail(cmid);
    }

    public Object resendMail(int cmid)
    {
        return verification.resendMail(cmid);
    }

    public Object responeDoctor(int cmid,String reason)
    {
        return verification.responeDoctor(cmid,reason);
    }

    public Object getWaitingForDoctor(int doctorCmid) {

        /**
         * Maor, why does this function require more than just doctor CMID? What's the logic
         * behind that... also looked at it and couldn't understand why does it need "status" in addition to Doctor CMID.
         * TODO - MAOR, Please clarify the above*/
        //dbController.getWaitingPatientsCMID(doctorCmid);
        //
        //need for each CMID - pull from db and grab relevant fields to be JSON'ed. TODO - What are they??
        return null;
    }
}
