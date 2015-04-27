package registrationModule.src.model;

import CommunicationModule.src.api.ICommController;
import CommunicationModule.src.controller.CommController_V1;
import CommunicationModule.src.model.CommToUsersFactory_V1;
import CommunicationModule.src.model.CommToUsers_V1;
import DatabaseModule.src.api.IDbComm_model;
import DatabaseModule.src.api.IDbController;
import DatabaseModule.src.controller.DbController_V1;

import org.json.JSONObject;
import registrationModule.src.api.IRegVerify_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by NAOR on 06/04/2015.
 */
public class RegVerify_V1 implements IRegVerify_model {
    private final int commControllerVersion = 1;
    private final int dbControllerVersion = 1;
    IDbController dbController = null;
    ICommController commController = null;
    //send the data
    //commToUsers.SendResponse();

    public RegVerify_V1()
    {
        commController = determineCommControllerVersion();
        dbController = determineDbControllerVersion();
    }

    public boolean VerifyDetail(int cmid){
        HashMap<String,String> member = new HashMap<String,String>();
        changeStatusToVerifyDetail(cmid);
        /*member.put("P_CommunityMembers.InternalID",new Integer(cmid).toString());
        HashMap<String,String> responseToDoctor = dbController.getUserByParameter(member);
        responseToDoctor.put("RequestID", "verifyPatient");
        filterDataForVerification(responseToDoctor);
        verifyDetailsDueToType(cmid,responseToDoctor);*/
        return true;
    }

    private void changeStatusToVerifyDetail(int cmid) {
        dbController.updateStatus(cmid,"'wait'","'verifying details'");
        //dbController.updateStatus(cmid,"'verifying email'","'verifying details'");
        /*
        HashMap<String,String> dataToPatient = new HashMap<String, String>();

        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();
        responseToPatient = sendResponeTOApp(dataToPatient,"wait",cmid);
        responseToPatient.put(1,dataToPatient);
        commController.setCommToUsers(responseToPatient,1);
        commController.SendResponse();*/
    }

    private boolean verifyDetailsDueToType(int cmid,HashMap<String,String> responseToDoctor)
   {
       String status =  responseToDoctor.get("'StatusName'");//need to change
       int s = Integer.parseInt(status);

       //int s = 0;
       switch (s) {
           //for Guardian
           case 2:
               Guardian(responseToDoctor);
           //for Ill
           case 0:
               Ill(responseToDoctor);
               break;
           //for doctor
           case 1:
               HashMap<String,String> doctorsAuthorizer =
                       dbController.getEmailOfDoctorsAuthorizer(responseToDoctor.get("state"));
               doctor(responseToDoctor,doctorsAuthorizer);
               break;
           default:
               break;
       }

       return true;
   }

    private void doctor(HashMap<String, String> memberDetails,
                        HashMap<String, String> doctorsAuthorizer) {

        String firstName = memberDetails.get("FirstName");
        String lastName = memberDetails.get("LastName");
        String licenseNumber = memberDetails.get("LicenseNumber");

        String emailAddress = doctorsAuthorizer.get("Email");
        String emailMessage  = "Dear authorizer,\n" +
                "Please confirm/reject the following doctor be a valid doctor:\n" +
                "First Name: " + firstName + ".\n" +
                "Last Name: " + lastName + ".\n" +
                "Licence Number: " + licenseNumber + ".\n\n" +
                "Thank you,\n" +
                "Socmed administration team.";
        String subject = "Doctor Authorization for Socmed App";

        ICommController commController = determineCommControllerVersion();
        commController.setCommToMail(emailAddress,emailMessage,subject);
        commController.sendEmail();
    }


    private void Guardian(HashMap<String,String> memberDetails) {
        //get doctor
        String doctorCmid = getDoctorCmid(memberDetails);
        HashMap<Integer,HashMap<String,String>> data =
                new HashMap<Integer,HashMap<String,String>>();

        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add(doctorCmid);

        //memberDetails.put("SendToCmid", doctorCmid);
        data.put(Integer.parseInt(memberDetails.get("InternalID")),memberDetails);
        commController.setCommToUsers(data,sendTo,1);
        commController.SendResponse();
    }

    private String getDoctorCmid(HashMap<String, String> memberDetails) {
        HashMap<String,String> whereConditions =  new HashMap<String, String>();
        whereConditions.put("DoctorID",memberDetails.get("DoctorID"));
        String doctorCmid = (dbController.getRowsFromTable(whereConditions,"P_Doctors")).get(1).get("CommunityMemberID");
        return doctorCmid;
    }

    private void Ill(HashMap<String,String> memberDetails)
    {
        //need filter memberDetails
        HashMap<Integer,HashMap<String,String>> data =
                new HashMap<Integer,HashMap<String,String>>();
        HashMap<String,String> filter = filterDataForVerification(memberDetails);
        data.put(1,filter);
        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add(filter.get("InternalID"));

        commController.setCommToUsers(data,sendTo,1);
        commController.SendResponse();
    }
    private HashMap<String,String> filterDataForVerification(HashMap<String, String> data)
    {
        HashMap<String, String> filter = new  HashMap<String, String>();
        HashMap<String,String> whereConditions =  new HashMap<String, String>();
        whereConditions.put("MedicalConditionID", data.get("MedicalConditionID"));

        String medicalConditionDescription = dbController.getRowsFromTable(whereConditions,"medicalConditions").get(1)
                .get("MedicalConditionDescription");

        filter.put("MedicalConditionDescription", medicalConditionDescription);




        HashMap<String,String> whereConditions2 =  new HashMap<String, String>();
        whereConditions.put("MedicationNum", data.get("MedicationNum"));

        String medicationName = dbController.getRowsFromTable(whereConditions2,"Medications").get(1)
                .get("MedicationName");

        filter.put("MedicationName", medicationName);



        for (String key : data.keySet()) {
            if (key == "FirstName" || key == "LastName" || key == "Street" ||
                    key ==  "HomePhoneNumber" || key == "Email"
             || key == "HouseNumber" || key == "ContactPhone" ||
                    key == "ZipCode" || key == "BirthDate" || key == "City" ||
                    key == "MobilePhoneNumber" || key == "State"
                    || key == "Gender"  || key == "Dosage")
                filter.put(key,data.get(key));

        }
        return filter;
    }

    public void resendMail(int cmid){

        HashMap<String,String> member = new HashMap<String,String>();
        member.put("P_CommunityMembers.InternalID",new Integer(cmid).toString());
        HashMap<String,String> details = dbController.getUserByParameter(member);
        if (details.get("StatusNum").equals("verifying email")) {
            emailNotExsist(cmid,details);
        }
        else
        {
            HashMap<Integer,HashMap<String,String>> responseToPatient =
                    new HashMap<Integer,HashMap<String,String>>();
            responseToPatient = sendResponeTOApp(null,"rejectResend",cmid);
            commController.setCommToUsers(responseToPatient,new ArrayList<String>(),1);
            commController.SendResponse();
        }
    }


    private void emailNotExsist(int cmid, HashMap<String, String> details) {
        String firstName = details.get("FirstName");
        String lastName = details.get("LastName");
        String emailAddress = details.get("Email");
        String emailMessage = "Dear " + firstName + "  " + lastName + ",\n";
        String subject = "Confirm your email for Socmed App";
        ICommController commController = determineCommControllerVersion();
        commController.setCommToMail(emailAddress, emailMessage, subject);
        commController.sendEmail();
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();
        responseToPatient = sendResponeTOApp(details,"verifying email",cmid);

        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add( new Integer(cmid).toString());


        commController.setCommToUsers(responseToPatient,sendTo,1);
        commController.SendResponse();
    }

    private HashMap<Integer,HashMap<String,String>> sendResponeTOApp(HashMap<String, String> details, String code
            , int cmid) {
        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", code);
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();

        responseToPatient.put(1,response);
        return responseToPatient;

    }


    public void proccesOfOkMember(int cmid)
    {
        dbController.updateStatus(cmid,"'verifying details'","'active'");
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();

        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add( new Integer(cmid).toString());

        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", "Active");


        getFrequency("LocationFrequency");
        getFrequency("ConnectServerFrequency");
        getFrequency("TimesToConectToServe");


        HashMap<Integer,HashMap<String,String>> a
                = dbController.getDefaultInEmergency(getState(cmid));

        /*HashMap<String,String> member = new HashMap<String,String>();
        member.put("P_CommunityMembers.InternalID",new Integer(cmid).toString());
        HashMap<String,String> details = dbController.getUserByParameter(member);
        */
        //responseToPatient = sendResponeTOApp(details,"confirmPatient",cmid);

        commController.setCommToUsers(responseToPatient, sendTo,1);
        commController.SendResponse();
    }

    private HashMap<Integer,HashMap<String,String>> getFrequency(String code) {
        HashMap<String,String> kindOfFrequency = new HashMap<String,String>();
        kindOfFrequency.put("Name",code);
        return dbController.getFrequency(kindOfFrequency);
    }

    private String getState(int cmid) {
        HashMap<String,String> member = new HashMap<String,String>();
        member.put("P_CommunityMembers.InternalID",new Integer(cmid).toString());
        HashMap<String,String> details = dbController.getUserByParameter(member);
        return details.get("State");
    }

    private ICommController determineCommControllerVersion(){
        switch (commControllerVersion) {
            //Communicate the DB to retrieve the data
            case 1: {
                return new CommController_V1();
            }
            default: {
                return null;
            }
        }
    }
    private IDbController determineDbControllerVersion(){
        switch (dbControllerVersion) {
            //Communicate the DB to retrieve the data
            case 1: {
                return new DbController_V1();
            }
            default: {
                return null;
            }
        }
    }

}
