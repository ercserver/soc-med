package registrationModule.src.model;

import DatabaseModule.src.api.IDbController;
import registrationModule.src.api.IRegVerify_model;
import Utilities.ModelsFactory;

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
        String status = getStatus(data);
        if (status.equals("verifying details"))
        {
            HashMap<String, String> dataToPatient = new HashMap<String, String>();
            dataToPatient.put("RequestID", "wait");
            responseToPatient.put(1, dataToPatient);
            return responseToPatient;
        }
        return null;
    }

    public HashMap<String,String> getPatientAndFillterDataToSendDoctor(int cmid) {
        HashMap<String, String> member = new HashMap<String, String>();
        member.put("P_CommunityMembers.community_member_id", new Integer(cmid).toString());
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
        String firstName = memberDetails.get("first_name");
        String lastName = memberDetails.get("last_name");
        String licenseNumber = memberDetails.get("license_number");

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
        if (code.equals("0"))
            return false;
        else
            return true;
    }

    private HashMap<String,String> filterDataForVerification(HashMap<String, String> data)
    {
        HashMap<String, String> filter = new  HashMap<String, String>();
        HashMap<String,String> whereConditions =  new HashMap<String, String>();
        whereConditions.put("medical_condition_id", data.get("medical_condition_id"));

        String medicalConditionDescription =
                dbController.getRowsFromTable(whereConditions,"medical_conditions").get(1)
                .get("medical_condition_description");

        filter.put("medical_condition_description", medicalConditionDescription);

        HashMap<String,String> whereConditions2 =  new HashMap<String, String>();
        whereConditions.put("medication_num", data.get("medication_num"));

        String medicationName =
                dbController.getRowsFromTable(whereConditions2,"medications").get(1)
                .get("medication_name");

        filter.put("medication_name", medicationName);

        for (String key : data.keySet()) {
            if (key == "first_name" || key == "last_name" || key == "street" ||
                    key ==  "home_phone_number" || key == "email_address"
                    || key == "house_number" || key == "contact_phone" ||
                    key == "zip_code" || key == "birth_date" || key == "city" ||
                    key == "mobile_phone_number" || key == "state"
                    || key == "gender"  || key == "dosage")
                filter.put(key,data.get(key));
        }
        return filter;
    }

    public boolean statusIsEqualTo(String s,HashMap <String,String> details) {
        return details.get("status_num").equals(s);
    }

    /***********for func resendMail*********************/
    public HashMap<String, String> getUserByCmid(int cmid) {

        HashMap<String, String> member = new HashMap<String, String>();
        member.put("P_CommunityMembers.community_member_id", new Integer(cmid).toString());
        HashMap<String, String> details = dbController.getUserByParameter(member);
        HashMap<Integer, HashMap<String, String>> reg_id = dbController.getRegIDsOfUser(cmid);
        String reg = "";
        for (Map.Entry<Integer,HashMap<String,String>> objs : reg_id.entrySet()){
            HashMap<String,String> obj = objs.getValue();
            reg = obj.get("reg_id");
        }
        details.put("reg_id",reg);
        return details;
    }

    public HashMap<String, String> getUserByMail(String mail) {

        HashMap<String, String> member = new HashMap<String, String>();
        member.put("P_CommunityMembers.email_address", "'" + mail + "'");
        HashMap<String, String> details = dbController.getUserByParameter(member);
        return details;
    }

    public String getStatus(HashMap<String, String> details)
    {
        String status_num = details.get("status_num");
        HashMap<String,String> whereConditions = new HashMap<String, String>();
        whereConditions.put("status_num", "'" + status_num + "'");
        HashMap<Integer, HashMap<String, String>> data
                = dbController.getRowsFromTable(whereConditions, "P_Statuses");

        for (Map.Entry<Integer,HashMap<String,String>> objs : data.entrySet()){
            HashMap<String,String> obj = objs.getValue();
            return obj.get("status_name");
         }
        return null;

    }


    public boolean checkCondForResendMail(HashMap<String, String> details, String email, int cmid) {
        if (details.get("status_num").equals("verifying email"))
            return false;
        if (getUserByMail(email) == null)
            return false;
        else
            return true;
    }

    /***********for func responeDoctor********************/


    public HashMap<Integer,HashMap<String,String>> proccesOfOkMember(int cmid)
    {
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();

        HashMap<String,String> response = new HashMap<String, String>();
        response.put("RequestID", "Active");

        responseToPatient.put(1, response);
        responseToPatient.put(2,getFrequency("'location_frequency'"));
        responseToPatient.put(3,getFrequency("'connect_server_frequency'"));
        responseToPatient.put(4,getFrequency("'times_to_conect_to_serve'"));

        responseToPatient.put(5, getDefaultInEmergency(getState(cmid)));

        System.out.println(responseToPatient.get(1));
        System.out.println(responseToPatient.get(2));
        System.out.println(responseToPatient.get(3));
        System.out.println(responseToPatient.get(4));
        System.out.println(responseToPatient.get(5));

        return responseToPatient;
    }

    public int checkIfDoctorIsaccept(String email)
    {
        HashMap<String, String> member = new HashMap<String, String>();
        member.put("P_CommunityMembers.email_address", email);
        HashMap<String, String> details = dbController.getUserByParameter(member);
        if (details.get("status_num").equals("reject by authentication"))
            return 0;
        if (details.get("status_num").equals("Active"))
            return 1;
        else
            // his status equal to verify email or details
            return 2;
    }

    public HashMap<Integer,HashMap<String,String>> verifySignIn(HashMap<String,String> details)
    {
        HashMap<String,String> conds = new HashMap<String,String>();
        conds.put("P_CommunityMembers.community_member_id", details.get("community_member_id"));
        conds.put("MembersLoginDetails.password", "'" + details.get("password") + "'");
        conds.put("MembersLoginDetails.email_address", "'" + details.get("email_address") + "'");
        // gets the user according to the givven sign-in data
        HashMap<String,String> user = dbController.getUserByParameter(conds);
        HashMap<Integer,HashMap<String,String>> response = new HashMap<Integer,HashMap<String,String>>();
        HashMap<String,String> res = new HashMap<String,String>();
        // wrong details(probably password)-rejects sign-in
        if (user == null)
            res.put("RequestID", "reject");
            // correct log-in details-accept sign-in
        else
            res.put("RequestID", "accept");
        response.put(1, res);
        return response;
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

    public HashMap<String, String> generateDataForAuthD(String access, String message, String subject, int authMethod) {
        //Generate data for the authentication object to be created according to the authentication method
        switch(authMethod){
            case 0:{
                return generateVerificationForMailD(access, message, subject);
            }
            case 1:{
                return generateVerificationForSMSD(access, message, subject);
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

    private String getState(int cmid) {
        HashMap<String,String> member = new HashMap<String,String>();
        member.put("P_CommunityMembers.community_member_id",new Integer(cmid).toString());
        HashMap<String,String> details = dbController.getUserByParameter(member);
        return details.get("state");
    }

    private HashMap<String, String> getDefaultInEmergency(String state) {
        HashMap<Integer,HashMap<String,String>> defult
                = dbController.getDefaultInEmergency(state);
        for (Map.Entry<Integer,HashMap<String,String>> objs : defult.entrySet()){
            HashMap<String,String> obj = objs.getValue();
            String code = obj.get("default_caller");
            HashMap<String,String> response = convertCodeToDefaultCallerSettings(code);
            return response;

        }
        return null;
    }

    public HashMap<String, String> convertCodeToDefaultCallerSettings(String code) {
        HashMap<String,String> defalut = new HashMap<String,String>();
        defalut.put("column_name","'default_caller'");
        defalut.put("enum_code", "'" + code + "'");
        defalut.put("table_name","'DefaultCallerSettings'");
        HashMap<Integer, HashMap<String, String>> data =
                dbController.getFromEnum(defalut);

        //System.out.println(response);
        for (Map.Entry<Integer,HashMap<String,String>> objs : data.entrySet()){
            HashMap<String,String> response = new HashMap<String,String>();
            HashMap<String,String> obj = objs.getValue();
            response.put("default_caller",obj.get("enum_value"));
            return response;
        }

        return null;
    }

    private HashMap<String,String> getFrequency(String code) {
        HashMap<String,String> response = new HashMap<String,String>();

        HashMap<String,String> kindOfFrequency = new HashMap<String,String>();
        kindOfFrequency.put("name",code);
        HashMap<Integer,HashMap<String,String>> freq
                = dbController.getFrequency(kindOfFrequency);
        if (freq == null)
            return null;
        for (Map.Entry<Integer,HashMap<String,String>> objs : freq.entrySet()){
            HashMap<String,String> obj = objs.getValue();

            response.put("name",obj.get("name"));
            response.put("frequency",obj.get("frequency"));
            return response;
        }
        return null;
    }

    private boolean doesDoctorExist(String doctorID) {
        HashMap<String,String> whereConditions = new HashMap<String, String>();
        whereConditions.put("doctor_id", doctorID);
        return (null != dbController.getRowsFromTable(whereConditions, "'Doctors'"));
    }

    //TODO- Not for prototype for future releases
    private HashMap<String, String>  generateVerificationForSMSD(String access, String message, String subject) {
        return null;
    }

    private HashMap<String,String> generateVerificationForMailD(String access, String message, String subject)
    {
        HashMap<String,String> generatedAuthMail = new HashMap<String, String>();
        generatedAuthMail.put("Subject", subject);
        generatedAuthMail.put("Message", message);
        generatedAuthMail.put("Email", access);

        return generatedAuthMail;
    }

    private HashMap<String,String> generateVerificationForMail(HashMap<String, String> data){
        String firstName = data.get("first_name");
        String lastName = data.get("last_name");
        String emailAddress = data.get("email_address");
        String emailMessage = "Dear " + firstName + "  " + lastName + ",\n\n" +
                "Thank you for registering to SocMed.\n" +
                "CMID: " + data.get("community_member_id\n") +
                "Password: " + data.get("Password\n") +
                "Please click the following link to complete your registration:\n" +
                generateMailLinkForVerifications(data);


        String emailSubject = "Confirm your email for Socmed App";

        HashMap<String,String> generatedAuthMail = new HashMap<String, String>();
        generatedAuthMail.put("Subject", emailSubject);
        generatedAuthMail.put("Message", emailMessage);
        generatedAuthMail.put("Email", emailAddress);

        return generatedAuthMail;
    }
    //TODO -
    private String generateMailLinkForVerifications(HashMap<String, String> data) {
        return null;
    }

    //TODO - Not for prototype for future releases only
    private HashMap<String,String> generateVerificationForSMS(HashMap<String, String> data){
        return null;
    }
}
