package registrationModule.src.model;

import CommunicationModule.src.model.CommToUsersFactory_V1;
import CommunicationModule.src.model.CommToUsers_V1;
import DatabaseModule.src.api.IDbComm_model;
import DatabaseModule.src.model.DbComm_V1;
import registrationModule.src.api.IRegVerify_model;

import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public class RegVerify_V1 implements IRegVerify_model {

    IDbComm_model model = null;


    //determine how to send the data
    CommToUsersFactory_V1 commToUsersFact = null;
    //send the data
    //commToUsers.SendResponse();

    public RegVerify_V1()
    {
        commToUsersFact = new CommToUsersFactory_V1();
        model = new DbComm_V1();
    }

    public boolean VerifyDetail(int cmid){
        //need to change to enum
        //changeDetail(cmid,"StatusNum","VerifyDetail");
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
       HashMap<String,String> memberDetails = model.getUserByParameter(member);
       data.put(cmid,memberDetails);
       System.out.println("hello");
//       String status =  memberDetails.get("StatusName");//need to change
  //     int s = Integer.parseInt(status);
/*
       int s = 0;
       switch (s) {
           //for Guardian
           case 0:

               Guardian(data);
           //for Ill
           case 1:
               Ill(data);
               break;
           //for doctor
           case 2:
               HashMap<String,String> doctorsAuthorizer =
                       model.getEmailOfDoctorsAuthorizer(memberDetails.get("state"));
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
        CommToUsers_V1 commToUsers = commToUsersFact.createComm(memberDetails,1 );
        commToUsers.SendResponse();
    }

    private void Guardian(HashMap<String,String> memberDetails) {
        //get doctor
        HashMap<Integer,HashMap<String,String>> data =
                new HashMap<Integer,HashMap<String,String>>();
        //find spesipic doctor for patient
        String cmidDoctor   = memberDetails.get("DoctorID");
        HashMap<String,String> member = new HashMap<String,String>();
        member.put("P_CommunityMembers.InternalID", cmidDoctor);
        HashMap<String,String> doctor = model.getUserByParameter(member);

        data.put(Integer.parseInt(memberDetails.get("InternalID")),memberDetails);
        CommToUsers_V1 commToUsers = commToUsersFact.createComm(data,1 );
        commToUsers.SendResponse();
    }

    private void Ill(HashMap<Integer,HashMap<String,String>> memberDetails)
    {
        //need filter memberDetails
        CommToUsers_V1 commToUsers = commToUsersFact.createComm(memberDetails,1 );
        commToUsers.SendResponse();
    }

    public String resendMail(String mail,int cmid){
        return null;
    }

    public void proccesOfOkMember(int cmid)
    {

    }//state1 common to all



}
