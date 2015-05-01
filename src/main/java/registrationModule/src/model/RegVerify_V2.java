package registrationModule.src.model;

import CommunicationModule.src.model.CommToUsersFactory_V1;
import CommunicationModule.src.model.CommToUsers_V1;
import DatabaseModule.src.api.IDbController;
import registrationModule.src.api.IRegVerify_model;
import Utilities.ModelsFactory;

import javax.swing.plaf.basic.BasicScrollPaneUI;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by User on 29/04/2015.
 */
public class RegVerify_V2 implements IRegVerify_model {

    IDbController dbController = null;

    public RegVerify_V2() {
        ModelsFactory models = new ModelsFactory();
        dbController = models.determineDbControllerVersion();
    }

    /***********for func verifyDetail*********************/

    public HashMap<Integer, HashMap<String, String>> changeStatusToVerifyDetailAndSendToApp(int cmid,
    HashMap<String, String> data) {

        HashMap<Integer, HashMap<String, String>> responseToPatient =
                new HashMap<Integer, HashMap<String, String>>();
        if (!statusIsEqualTo("verifying details",data))
        {
            //dbController.updateStatus(cmid,"'verifying details'","'verifying email'");
            dbController.updateStatus(cmid, "'verifying email'", "'verifying details'");
            HashMap<String, String> dataToPatient = new HashMap<String, String>();
            dataToPatient.putAll(addRequestID(dataToPatient, "wait"));
            responseToPatient.put(1, dataToPatient);
            return responseToPatient;

        }
        return null;
    }

    public HashMap<String,String> getPatientAndFillterDataToSendDoctor(int cmid) {
        HashMap<String, String> member = new HashMap<String, String>();
        member.put("P_CommunityMembers.CommunityMemberID", new Integer(cmid).toString());
        HashMap<String, String> responseToDoctor = dbController.getUserByParameter(member);
        responseToDoctor.put("RequestID", "verifyPatient");
        return filterDataForVerification(responseToDoctor);
    }

      public ArrayList<String> iFIsADoctorBuildMail(int cmid, String code,HashMap<String,String> data) {

          if (ifTypeISDoctor(code)) {
              HashMap<String, String> doctorsAuthorizer =
                      dbController.getEmailOfDoctorsAuthorizer(data.get("state"));
              return generateMailForVerificationDoctor(data, doctorsAuthorizer);
          }
          return null;
      }

    private ArrayList<String> generateMailForVerificationDoctor(HashMap<String, String> memberDetails,
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


    private boolean ifTypeISDoctor(String regid) {
        if (regid.equals("0"))
            return true;
        else
            return false;
    }

    public boolean ifTypeISPatientOrGuardian(String code) {
        if (code.equals("Patient") || code.equals("Guardian"))
            return true;
        else
            return false;
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
                    key ==  "HomePhoneNumber" || key == "EmailAddress"
                    || key == "HouseNumber" || key == "ContactPhone" ||
                    key == "ZipCode" || key == "BirthDate" || key == "City" ||
                    key == "MobilePhoneNumber" || key == "State"
                    || key == "Gender"  || key == "Dosage")
                filter.put(key,data.get(key));

        }
        return filter;
    }

    private HashMap<String,String> addRequestID(HashMap<String, String> details, String code) {
        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", code);
        return response;
    }

    public HashMap<Integer,HashMap<String,String>> BuildResponeWithOnlyRequestID(HashMap<String, String> details,
            String code)
    {
        HashMap<Integer,HashMap<String,String>> res = new HashMap<Integer,HashMap<String,String>>();
        res.put(1,addRequestID(details,code));
        return res;
    }


    public boolean statusIsEqualTo(String s,HashMap <String,String> details) {
        return details.get("StatusNum").equals(s);
    }

    /***********for func resendMail*********************/
    public HashMap<String, String> getUserByCmid(int cmid) {

        HashMap<String, String> member = new HashMap<String, String>();
        member.put("P_CommunityMembers.CommunityMemberID", new Integer(cmid).toString());
        HashMap<String, String> details = dbController.getUserByParameter(member);
        return details;
    }





    /***********for func responeDoctor********************/
    public HashMap<Integer,HashMap<String,String>> buildRejectMessage(int cmid, String Reason) {
        dbController.updateStatus(cmid, "'verifying details'", "'active'");
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();
        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", "Reject");
        response.put("Reason", Reason);
        responseToPatient.put(1, response);
        return responseToPatient;
    }

    public HashMap<Integer,HashMap<String,String>> proccesOfOkMember(int cmid)
    {
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();

        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", "Active");


        response.putAll(getFrequency("LocationFrequency"));
        response.putAll(getFrequency("ConnectServerFrequency"));
        response.putAll(getFrequency("TimesToConectToServe"));

        response.putAll(getDefaultInEmergency(getState(cmid)));

        responseToPatient.put(1,response);
        return responseToPatient;
    }


    private String getState(int cmid) {
        HashMap<String,String> member = new HashMap<String,String>();
        member.put("P_CommunityMembers.CommunityMemberID",new Integer(cmid).toString());
        HashMap<String,String> details = dbController.getUserByParameter(member);
        return details.get("State");
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




    public HashMap<Integer,HashMap<String,String>> verifySignIn(HashMap<String,String> details)
    {
        HashMap<String,String> conds = new HashMap<String,String>();
        conds.put("P_CommunityMembers.CommunityMemberID", details.get("CommunityMemberID"));
        conds.put("MembersLoginDetails.Password", "'" + details.get("Password") + "'");
        conds.put("MembersLoginDetails.EmailAddress", "'" + details.get("EmailAddress") + "'");
        HashMap<String,String> user = dbController.getUserByParameter(conds);
        HashMap<Integer,HashMap<String,String>> response = new HashMap<Integer,HashMap<String,String>>();
        HashMap<String,String> res = new HashMap<String,String>();
        if (user == null)
            res.put("RequestID", "reject");
        else
            res.put("RequestID", "accept");
        response.put(1, res);
        return response;
    }

    public int checkIfDoctorIsaccept(String email)
    {
        HashMap<String, String> member = new HashMap<String, String>();
        member.put("P_CommunityMembers.EmailAddress", email);
        HashMap<String, String> details = dbController.getUserByParameter(member);
        if (details.get("StatusNum").equals("reject by authentication"))
            return 0;
        if (details.get("StatusNum").equals("active"))
            return 1;
        else
            // his status equal to verify email or details
            return 2;
    }

    public ArrayList<String> verifyFilledForm(HashMap<String, String> filledForm) {
        ArrayList<String> errorMessages = new ArrayList<String>();
        String userType = filledForm.get("userType");
        if (userType.equals("Patient")) {
            if(!doesDoctorExist(filledForm.get("DoctorID"))){
                errorMessages.add("Doctor does not exist!");
            }
            //if{....}
            //more things to verify....
            //

        }/*
        else if(userType.equals("Doctor")){
          ........
            ........need to verify something in this stage?
              ........
        }
        else if(userType.equals("EMS")){
          ........
            ........need to verify something in this stage?
              ........
        }
        else if(userType.equals("Apotropus")){
          ........
            ........need to verify something in this stage?
              ........
        }
        */
        return errorMessages;
    }

    private boolean doesDoctorExist(String doctorID) {
        HashMap<String,String> whereConditions = new HashMap<String, String>();
        whereConditions.put("DoctorID", doctorID);
        return (null != dbController.getRowsFromTable(whereConditions, "'Doctors'"));
    }

    public HashMap<String, String> generateDataForAuth(HashMap<String, String> filledForm, int authMethod) {
        //Generate data for the authentication object to be created according to the authentication method
        switch(authMethod){
            case 0:{
                return generateVerificationForMail(filledForm);
            }
            case 1:{
                return generateVerificationForSMS(filledForm);
            }/*
            case x:{
                return generateVerificationForXXXXX();
            }
            */
            default:{
                return null;
            }
        }
    }

    private HashMap<String,String> generateVerificationForMail(HashMap<String, String> data){
        String firstName = data.get("FirstName");
        String lastName = data.get("LastName");
        String emailAddress = data.get("EmailAddress");
        String emailMessage = "Dear " + firstName + "  " + lastName + ",\n";
        String emailSubject = "Confirm your email for Socmed App";

        HashMap<String,String> generatedAuthMail = new HashMap<String, String>();
        generatedAuthMail.put("Subject", emailSubject);//
        generatedAuthMail.put("Message", emailMessage);
        generatedAuthMail.put("Email", emailAddress);

        return generatedAuthMail;
    }

    //TODO - Not for prototype for future releases only
    private HashMap<String,String> generateVerificationForSMS(HashMap<String, String> data){
        return null;
    }
}





