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
        /*dbController.updateStatus(cmid,"'verifying email'","'verifying details'");
        HashMap<String,String> dataToPatient = new HashMap<String, String>();
        dataToPatient.put("RequestID", "wait");
        dataToPatient.put("SendToCmid", new Integer(cmid).toString());
        //need filter memberDetails
        HashMap<Integer,HashMap<String,String>> responseToPatient =
                new HashMap<Integer,HashMap<String,String>>();
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
        HashMap<Integer,HashMap<String,String>> data =
                new HashMap<Integer,HashMap<String,String>>();
        //find spesipic doctor for patient
        HashMap<String,String> whereConditions =  new HashMap<String, String>();
        whereConditions.put("DoctorID",memberDetails.get("DoctorID"));


        String doctorCmid = (dbController.getRowsFromTable(whereConditions,"P_Doctors")).get(1).get("CommunityMemberID");

        memberDetails.put("SendToCmid", doctorCmid);



        data.put(Integer.parseInt(memberDetails.get("InternalID")),memberDetails);
        commController.setCommToUsers(data, 1);

        commController.SendResponse();
    }

    private void Ill(HashMap<String,String> memberDetails)
    {
        //need filter memberDetails
        HashMap<Integer,HashMap<String,String>> data =
                new HashMap<Integer,HashMap<String,String>>();
        HashMap<String,String> filter = filterDataForVerification(memberDetails);
        data.put(1,filter);


        commController.setCommToUsers(data,1);
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
    public String resendMail(String mail,int cmid){
        return null;
    }

    public void proccesOfOkMember(int cmid)
    {

    }//state1 common to all

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
