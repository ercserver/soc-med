package registrationModule.src.controller;

import CommunicationModule.src.api.ICommController;
import DatabaseModule.src.api.IDbController;

import registrationModule.src.api.IRegController;
import registrationModule.src.api.IRegRequest_model;
import registrationModule.src.api.IRegVerify_model;

import registrationModule.src.utilities.ModelsFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by NAOR on 06/04/2015.dd
 */
public class RegController_V1 implements IRegController {
    private IRegRequest_model registrator = null;
    private IRegVerify_model verification = null;
    private IDbController dbController = null;
    private ICommController commController = null;

    public RegController_V1() {
        ModelsFactory models = new ModelsFactory();
        commController = models.determineCommControllerVersion();
        dbController = models.determineDbControllerVersion();
        registrator = models.determineRegRequestVersion();
        verification = models.determineRegVerifyVersion();
    }

    public Object getRegDetails(HashMap<String, String> request) {
        //generate data to send
        HashMap<Integer, HashMap<String, String>> dataToSend = new HashMap<Integer, HashMap<String, String>>();
        HashMap<String, String> data = registrator.regDetailsRequest(request);
        ArrayList<String> sendTo = sendTo(data);
        dataToSend.put(1, data);
        //determine how to send the data
        commController.setCommToUsers(dataToSend, sendTo, false);

        //send the data
        return commController.sendResponse();
    }

    public Object handleReg(HashMap<String, String> filledForm) {
        HashMap<Integer, HashMap<String, String>> dataToSend = new HashMap<Integer, HashMap<String, String>>();
        //if the user exists (registration model decides how to determine that)
        if (registrator.doesUserExist(filledForm)) {
            //add errormessage and response coode to the data to be sent back
            dataToSend.get(1).put("ErrorMessage", "An active user with this mail already exists");
            //(what code is it?)
            dataToSend.get(1).put("ResponseCode", "XXXXXXXX");
        }
        //User does not exist
        else {
            //Add the new community member (a new CmID is generated)
            String newCmid = Integer.toString(dbController.addNewCommunityMember(filledForm));
            //Separate according to user type and handle accordingly.... use the Verification Module for verification processes
            verifyFilledForm(filledForm, newCmid);
        }
        ArrayList<String> sendTo = sendTo(filledForm);
        dataToSend.put(1, filledForm);
        //determine how to send the data
        commController.setCommToUsers(dataToSend, sendTo, false);

        //send the data
        return commController.sendResponse();
    }

    private void verifyFilledForm(HashMap<String, String> filledForm, String newCmid) {

        //ArrayList<String> generateVerEmail = verification.generateMailForVerification(filledForm);
    }

    private ArrayList<String> sendTo(HashMap<String, String> data) {
        String regID = data.get("RegID");
        ArrayList<String> sendTo = null;
        if (null != regID) {
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
        String code = data.get("RequestID");
        ArrayList<String> target = new ArrayList<String>();
        target.add(regid);
        if (checkCmidAndPassword(password, cmid)) {
            changeStatusToVerifyDetailAndSendToApp(cmid,code, target,data);
            HashMap<String,String> dataFilter = verification.getPatientAndFillterDataToSendDoctor(cmid);
            ArrayList<String> mail = verification.iFIsADoctorBuildMail(cmid, code, dataFilter);
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

    private void sendMail(String emailAddress, String emailMessage, String subject) {
        commController.setCommToMail(emailAddress, emailMessage, subject);
        commController.sendEmail();
    }

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
                ArrayList<String> mail =  verification.generateMailForVerificationEmail(details);
                String emailAddress = mail.get(0);
                String emailMessage = mail.get(1);
                String subject =   mail.get(2);
                sendMail(emailAddress,emailMessage,
                        subject);
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
    public Object responeDoctor(HashMap<String, String> data) {

        int cmid = Integer.parseInt(data.get("CommunityMemberID"));
        String reason = data.get("Reason");
        String password = data.get("Password");
        String regid = data.get("RegID");
        if (checkCmidAndPassword(password, cmid)) {

        }   //verification.responeDoctor(cmid, reason,regid);
        return null;
    }


    private boolean checkCmidAndPassword(String password, int cmid) {
        HashMap<String,String> member = new HashMap<String,String>();
        member.put("CommunityMemberID",new Integer(cmid).toString());
        HashMap<String,String> data = dbController.getUserByParameter(member);
        String pas = data.get("Password");
        return pas.equals(password);
    }

    public Object signIn(HashMap<String,String> details)
    {
        HashMap<Integer,HashMap<String,String>> response = verification.verifySignIn(details);
        // Sign in of doctor/ems
        if(details.get("RegID") == "0")
        {
            commController.setCommToUsers(response, null, false);
        }
        else
        {
            ArrayList<String> target = new ArrayList<String>();
            target.add(details.get("RegID"));
            commController.setCommToUsers(response, target, false);
        }
        return commController.sendResponse();
    }
}

