package registrationModule.src.model;

import CommunicationModule.src.api.ICommController;
import DatabaseModule.src.api.IDbController;

import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.utilities.ModelsFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
/**
 * Created by NAOR on 06/04/2015.
 */
/*
public class RegVerify_V1 implements IRegVerify_model {

    IDbController dbController = null;

    public RegVerify_V1()
    {
        ModelsFactory models = new ModelsFactory();
        dbController = models.determineDbControllerVersion();
    }

    public Object verifyDetail(int cmid){
        HashMap<String,String> member = new HashMap<String,String>();
        if (!statusIsEqualTo("verifying details")) {
            changeStatusToVerifyDetail(cmid);
        }
        member.put("CommunityMemberID",new Integer(cmid).toString());
        HashMap<String,String> responseToDoctor = dbController.getUserByParameter(member);
        responseToDoctor.put("RequestID", "verifyPatient");
        filterDataForVerification(responseToDoctor);
        return verifyDetailsDueToType(cmid,responseToDoctor);
      }


    private void changeStatusToVerifyDetail(int cmid) {
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new  HashMap<Integer,HashMap<String,String>>();
        //dbController.updateStatus(cmid,"'verifying details'","'verifying email'");
       dbController.updateStatus(cmid,"'verifying email'","'verifying details'");

        HashMap<String,String> dataToPatient = new HashMap<String, String>();


        dataToPatient.putAll(sendResponeTOApp(dataToPatient, "wait", cmid));

        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add(new Integer(cmid).toString());

        responseToPatient.put(1,dataToPatient);

        commController.setCommToUsers(responseToPatient,sendTo,1);
        commController.SendResponse();

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
               return generateMailForVerificationDoctor(responseToDoctor,
                       doctorsAuthorizer);
               break;
           default:
               break;
       }

       return true;
   }

    public ArrayList<String> generateMailForVerificationDoctor(HashMap<String, String> memberDetails,
                                                               HashMap<String, String> doctorsAuthorizer){
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

        ArrayList<String> emailDetails = new ArrayList<String>();
        emailDetails.add(emailAddress);
        emailDetails.add(emailMessage);
        emailDetails.add(subject);

        return emailDetails;
    }
/*
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
    }
*/
/*
    private void Guardian(HashMap<String,String> memberDetails) {
        //get doctor
        String doctorCmid = getDoctorCmid(memberDetails);
        HashMap<Integer,HashMap<String,String>> data =
                new HashMap<Integer,HashMap<String,String>>();

        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add(doctorCmid);

        //memberDetails.put("SendToCmid", doctorCmid);
        data.put(Integer.parseInt(memberDetails.get("CommunityMemberID")), memberDetails);
        commController.setCommToUsers(data, sendTo, 1);
        commController.sendResponse();
    }

    private String getDoctorCmid(HashMap<String, String> memberDetails) {
        HashMap<String,String> whereConditions =  new HashMap<String, String>();
        whereConditions.put("DoctorID", memberDetails.get("DoctorID"));
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
        sendTo.add(filter.get("CommunityMemberID"));

        commController.setCommToUsers(data, sendTo, 1);
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

    public Object resendMail(int cmid){

        HashMap<String,String> member = new HashMap<String,String>();
        member.put("CommunityMemberID", new Integer(cmid).toString());
        HashMap<String,String> details = dbController.getUserByParameter(member);
        if (details.get("StatusNum").equals("verifying email")) {
            emailNotExsist(cmid,details);
        }
        else
        {
            HashMap<Integer,HashMap<String,String>> responseToPatient =
                    new HashMap<Integer,HashMap<String,String>>();
            responseToPatient.put(1, sendResponeTOApp(null, "rejectResend", cmid));
            commController.setCommToUsers(responseToPatient,new ArrayList<String>(),1);
            commController.sendResponse();
        }
    }







    public ArrayList<String> generateMailForVerificationEmail(HashMap<String, String> details){
        String firstName = details.get("FirstName");
        String lastName = details.get("LastName");
        String emailAddress = details.get("Email");
        String emailMessage = "Dear " + firstName + "  " + lastName + ",\n";
        String subject = "Confirm your email for Socmed App";

        ArrayList<String> emailDetails = new ArrayList<String>();
        emailDetails.add(emailAddress);
        emailDetails.add(emailMessage);
        emailDetails.add(subject);

        return emailDetails;
    }

    private void emailNotExsist(int cmid, HashMap<String, String> details) {
        String firstName = details.get("FirstName");
        String lastName = details.get("LastName");
        String emailAddress = details.get("Email");
        String emailMessage = "Dear " + firstName + "  " + lastName + ",\n";
        String subject = "Confirm your email for Socmed App";

        commController.setCommToMail(emailAddress, emailMessage, subject);
        commController.sendEmail();
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();

        responseToPatient.put(1, sendResponeTOApp(details, "verifying email", cmid));

        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add( new Integer(cmid).toString());


        commController.setCommToUsers(responseToPatient, sendTo, 1);
        commController.sendResponse();
    }

    private HashMap<String,String> sendResponeTOApp(HashMap<String, String> details, String code
            , int cmid) {
        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", code);
        /*HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();

        responseToPatient.put(1,response);
        */
/*
        return response;

    }

*/
/*
    public Object responeDoctor(int cmid,String reason)
    {
        if (reason == null)
            proccesOfOkMember(cmid);
        else
        {
            sendRejectMessage(cmid,reason);
        }
    }

    private void sendRejectMessage(int cmid, String Reason) {
        dbController.updateStatus(cmid,"'verifying details'","'active'");
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();

        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add(new Integer(cmid).toString());

        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", "Reject");
        response.put("Reason", Reason);
        responseToPatient.put(1,response);

        commController.setCommToUsers(responseToPatient, sendTo,1);
        commController.SendResponse();
    }


    private void proccesOfOkMember(int cmid)
    {
        dbController.updateStatus(cmid,"'verifying details'","'active'");
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();

        ArrayList<String> sendTo = new  ArrayList<String>();
        sendTo.add( new Integer(cmid).toString());

        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", "Active");


        response.putAll(getFrequency("LocationFrequency"));
        response.putAll(getFrequency("ConnectServerFrequency"));
        response.putAll(getFrequency("TimesToConectToServe"));

        response.putAll(getDefaultInEmergency(getState(cmid)));

        responseToPatient.put(1,response);

        commController.setCommToUsers(responseToPatient, sendTo,1);
        commController.SendResponse();
    }

    private HashMap<String, String> getDefaultInEmergency(String state) {
        HashMap<Integer,HashMap<String,String>> defult
                = dbController.getDefaultInEmergency(state);
        for (Map.Entry<Integer,HashMap<String,String>> objs : defult.entrySet()){
            HashMap<String,String> obj = objs.getValue();
            return obj;
        }
        return null;
    }

    private HashMap<String,String> getFrequency(String code) {
        HashMap<String,String> kindOfFrequency = new HashMap<String,String>();
        kindOfFrequency.put("Name",code);
        HashMap<Integer,HashMap<String,String>> freq
                = dbController.getFrequency(kindOfFrequency);
        for (Map.Entry<Integer,HashMap<String,String>> objs : freq.entrySet()){
            HashMap<String,String> obj = objs.getValue();
            return obj;
        }
        return null;
    }

    private String getState(int cmid) {
        HashMap<String,String> member = new HashMap<String,String>();
        member.put("CommunityMemberID",new Integer(cmid).toString());
        HashMap<String,String> details = dbController.getUserByParameter(member);
        return details.get("State");
    }


}
*/