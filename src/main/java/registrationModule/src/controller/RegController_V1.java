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

    //do
    public Object verifyDetail(HashMap<String, String> data) {
        int cmid = Integer.parseInt(data.get("CommunityMemberID"));
        String password = data.get("Password");
        String regid = data.get("RegID");
        //String code = data.get("RequestID");
        ArrayList<String> target = new ArrayList<String>();
        target.add(regid);
        if (checkCmidAndPassword(password, cmid)) {
            changeStatusToVerifyDetailAndSendToApp(cmid,regid, target,data);
            HashMap<String,String> dataFilter = verification.getPatientAndFillterDataToSendDoctor(cmid);
            ArrayList<String> mail = verification.iFIsADoctorBuildMail(cmid, regid, dataFilter);
            if (null != mail )
            {
                String emailAddress = mail.get(0);
                String emailMessage = mail.get(1);
                String subject =   mail.get(2);
                sendMail(emailAddress,emailMessage,
                        subject);
            }
        }
        return null;
    }

    /*private void sendMail(String emailAddress, String emailMessage, String subject) {
        commController.setCommToMail(emailAddress, emailMessage, subject);
        commController.sendEmail();
    }*/

    private void changeStatusToVerifyDetailAndSendToApp(int cmid, String code,
                                                        ArrayList<String> target,
                                                        HashMap<String, String> data) {
        if(verification.ifTypeISPatientOrGuardian(code)) {
            commController.setCommToUsers(verification.changeStatusToVerifyDetailAndSendToApp(cmid,data),
                    target, false);
            commController.sendResponse();
        }
    }

    public Object resendMail(HashMap<String, String> data) {
        int cmid = Integer.parseInt(data.get("CommunityMemberID"));
        String password = data.get("Password");
        String regid = data.get("RegID");
        ArrayList<String> target = new ArrayList<String>();
        target.add(regid);
        if (checkCmidAndPassword(password, cmid)) {
            HashMap<String,String> details = verification.getUserByCmid(cmid);
            if (details.get("StatusNum").equals("verifying email")) {
                int authMethod = dbController.getAuthenticationMethod("Israel");
                HashMap<String, String> mail =  verification.generateDataForAuth(details, authMethod);
                ICommController commAuthMethod = new ModelsFactory().determineCommControllerVersion();
                commAuthMethod.setCommOfficial(data,authMethod);
                //Communicate authorization (email/sms/...)
                commAuthMethod.sendMessage();
               /* String emailAddress = mail.get(0);
                String emailMessage = mail.get(1);
                String subject =   mail.get(2);
                sendMail(emailAddress,emailMessage,
                        subject);*/
            }
            else
            {
                if(ifTypeISPatientOrGuardian(regid)) {
                    commController.setCommToUsers(verification.BuildResponeWithOnlyRequestID(
                            data,"rejectResend"),target,false);
                    commController.sendResponse();
                }
            }
        }
        return null;
    }

    private boolean ifTypeISPatientOrGuardian(String regid) {
        return !regid.equals("0");
    }

    //need to do
    public Object responeByDoctor(HashMap<String, String> data) {
        HashMap<Integer,HashMap<String,String>> response =
                new HashMap<Integer,HashMap<String,String>>();
        int cmid = Integer.parseInt(data.get("CommunityMemberID"));
        String reason = data.get("Reason");
        String password = data.get("Password");
        String regid = data.get("RegID");
        ArrayList<String> target = new ArrayList<String>();
        target.add(regid);
        if (checkCmidAndPassword(password, cmid)) {
            if (reason == null) {
                dbController.updateStatus(cmid, "'verifying details'", "'active'");
                if (verification.ifTypeISPatientOrGuardian(regid)) {
                    response =  verification.proccesOfOkMember(cmid);
                    commController.setCommToUsers(response, target, false);
                    commController.sendResponse();
                }
            }
            else
            {
                //if is a doctor
                if (!verification.ifTypeISPatientOrGuardian(regid))
                {
                    dbController.updateStatus(cmid, "'verifying details'", "'reject by authentication'");
                    return null;
                }
                else {
                    response = verification.buildRejectMessage(cmid, reason);
                    commController.setCommToUsers(response, target, false);
                    commController.sendResponse();
                }
            }
        }   //verification.responeDoctor(cmid, reason,regid);
        return null;
    }

    private boolean checkCmidAndPassword(String password, int cmid) {
        HashMap<String,String> member = new HashMap<String,String>();
        member.put("P_CommunityMembers.CommunityMemberID",new Integer(cmid).toString());
        HashMap<String,String> data = dbController.getUserByParameter(member);
        String email = data.get("EmailAddress");
        data = dbController.getLoginDetails("'" +email + "'");
        String pas = data.get("Password");
        return pas.equals(password);
    }

    public Object responeToDoctorIfHeAccept(HashMap<String,String> details)
    {
        HashMap<Integer,HashMap<String,String>> response = new
                HashMap<Integer,HashMap<String,String>>();

        String email = details.get("EmailAddress");
        //reject
        int type = verification.checkIfDoctorIsaccept(email);
        if (type == 0) {
            int cmid = Integer.parseInt(details.get("CommunityMemberID"));
            dbController.deleteUser(cmid);
            response = verification.buildRejectMessage(cmid, "reject by authentication");
            commController.setCommToUsers(response, null, false);
        }
        //accept
        if (type == 1)
        {
            response = verification.BuildResponeWithOnlyRequestID(details,"Active");
            commController.setCommToUsers(response, null, false);

        }
        //in other status
        else
        {
            // is equal to 2
            response = verification.BuildResponeWithOnlyRequestID(details,"wait");
            commController.setCommToUsers(response, null, false);
        }
        return commController.sendResponse();
    }

    //TODO - Shmulit: need to implement resending of email or SMS
    public Object resendAuth(int cmid){
        return verification.resendAuth(cmid);
    }

    public Object getWaitingForDoctor(int doctorCmid) {
        //Pull from the db the list of patient that are pending the doctor's confirmation
        ArrayList<String> listOfPatients = dbController.getWaitingPatientsCMID(doctorCmid);

        HashMap<Integer,HashMap<String,String>> response = new HashMap<Integer,HashMap<String,String>>();
        //for each cmid in the list received - filter fields and add to the response
        int index = 1;
        for(String currCmid : listOfPatients){
            HashMap<String,String> whereConditions = new HashMap<String, String>();
            whereConditions.put("community_member_id", "'" + currCmid + "'");
            response.put(index,registrator.filterFieldsForDoctorAuth(dbController.getUserByParameter(whereConditions)));
            index++;
        }
        //determine how to send the data - initiated communication so use "false"
        commController.setCommToUsers(response,null,false);
        //send the data
        return commController.sendResponse();
    }

    public Object signIn(HashMap<String,String> details)
    {
        // verify log-in details
        HashMap<Integer,HashMap<String,String>> response = verification.verifySignIn(details);

        ArrayList<String> target = new ArrayList<String>();
        // Sign in of doctor/ems
        if(details.get("reg_id") == "0")
        {
            commController.setCommToUsers(response, null, false);
        }
        // Sign-in of patient
        else
        {
            target.add(details.get("reg_id"));
            commController.setCommToUsers(response, target, false);
        }
        // Sends response to the proper user
        return commController.sendResponse();
    }
}
