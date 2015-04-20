package registrationModule.src.model;

import CommunicationModule.src.api.ICommController;
import CommunicationModule.src.controller.CommController_V1;
import CommunicationModule.src.model.CommToUsersFactory_V1;
import CommunicationModule.src.model.CommToUsers_V1;
import DatabaseModule.src.api.IDbComm_model;
import DatabaseModule.src.api.IDbController;
import DatabaseModule.src.controller.DbController_V1;
import DatabaseModule.src.model.DbComm_V1;
import registrationModule.src.api.IRegVerify_model;

import java.util.HashMap;

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
        //need to change to enum
        //updateStatus(cmid,"'verifying email'" , "'verifying details'");
        verifyDetailsDueToType(cmid);
        return true;
    }
   private boolean verifyDetailsDueToType(int cmid)
   {
       HashMap<Integer,HashMap<String,String>> data =
               new HashMap<Integer,HashMap<String,String>>();
       HashMap<String,String> member = new HashMap<String,String>();
       //String c = "'" + new Integer(cmid).toString() + "'";
       member.put("P_CommunityMembers.InternalID",new Integer(cmid).toString());
       HashMap<String,String> memberDetails = dbController.getUserByParameter(member);
       data.put(cmid,memberDetails);
       System.out.println("hello");
//       String status =  memberDetails.get("StatusName");//need to change
  //     int s = Integer.parseInt(status);
/*
       int s = 0;
       switch (s) {
           //for Guardian
           case 2:
               Guardian(data);
           //for Ill
           case 0:
               Ill(data);
               break;
           //for doctor
           case 1:
               HashMap<String,String> doctorsAuthorizer =
                       dbController.getEmailOfDoctorsAuthorizer(memberDetails.get("state"));
               doctor(data,doctorsAuthorizer);
               break;
           default:
               break;
       }
*/
       return true;
   }

    private void doctor(HashMap<Integer, HashMap<String, String>> memberDetails,
                        HashMap<String, String> doctorsAuthorizer) {
        String emailAddress = doctorsAuthorizer.get("Email");
        String emailMessage  = null;
        String subject = null;


        ICommController commController = determineCommControllerVersion();
        commController.setCommToMail(emailAddress,emailMessage,subject);

        commController.sendEmail();
    }


    private void Guardian(HashMap<String,String> memberDetails) {
        //get doctor
        HashMap<Integer,HashMap<String,String>> data =
                new HashMap<Integer,HashMap<String,String>>();
        //find spesipic doctor for patient
        String cmidDoctor   = memberDetails.get("DoctorID");
        HashMap<String,String> member = new HashMap<String,String>();
        member.put("P_CommunityMembers.InternalID", cmidDoctor);
        HashMap<String,String> doctor = dbController.getUserByParameter(member);

        data.put(Integer.parseInt(memberDetails.get("InternalID")),memberDetails);
        commController.setCommToUsers(data, 1);

        commController.SendResponse();
    }

    private void Ill(HashMap<String,String> memberDetails)
    {
        //need filter memberDetails
        HashMap<Integer,HashMap<String,String>> data =
                new HashMap<Integer,HashMap<String,String>>();
        data.put(Integer.parseInt(memberDetails.get("InternalID")),
                memberDetails);

        commController.setCommToUsers(data,1);
        commController.SendResponse();
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
