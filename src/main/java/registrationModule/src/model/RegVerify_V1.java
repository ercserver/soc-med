package registrationModule.src.model;

import CommunicationModule.src.model.CommToUsersFactory_V1;
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
       HashMap<String,String> member = new HashMap<String,String>();
       member.put("InternalID",new Integer(cmid).toString());
       HashMap<String,String> memberDetails = model.getUserByParameter(member);
       System.out.println("hello");
//       String status =  memberDetails.get("status");//need to change
  //     int s = Integer.parseInt(status);
/*
       int s = 0;
       switch (s) {
           //for Ill
           case 0:
               //Ill(memberDetails);
               break;
           //for doctor
           case 1:
               break;
           //for Guardian
           case 2:
               break;
           default:
               break;
       }
       */
       return true;
   }

    private void Ill(HashMap<String,String> memberDetails)
    {
        //CommToUsers_V1 commToUsers = commToUsersFact.createComm(memberDetails,1 );

    }

    public String resendMail(String mail,int cmid){
        return null;
    }

    public void proccesOfOkMember(int cmid)
    {

    }//state1 common to all

    /**
     * Created by User on 16/04/2015.
     */
    public static class t {
        public static void main(String[] args) {
            System.out.println("hello");
            //RegVerify_V1 v = new RegVerify_V1();
            //v.VerifyDetail(1002);
        }
    }
}
