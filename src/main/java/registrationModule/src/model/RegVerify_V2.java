package registrationModule.src.model;

import DatabaseModule.src.api.IDbController;
import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.utilities.ModelsFactory;

import java.util.ArrayList;
import java.util.HashMap;

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
        member.put("CommunityMemberID", new Integer(cmid).toString());
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


    private boolean ifTypeISDoctor(String code) {
        if (code.equals("Docror"))
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
                    key ==  "HomePhoneNumber" || key == "Email"
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
        member.put("CommunityMemberID", new Integer(cmid).toString());
        HashMap<String, String> details = dbController.getUserByParameter(member);
        return details;
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

    /***********for func responeDoctor********************/


}





