package registrationModule.src.api;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify_model {

    /***********for func verifyDetail*********************/

    HashMap<Integer,HashMap<String,String>> changeStatusToVerifyDetailAndSendToApp(int cmid,
                                                                                   HashMap<String, String> data);
    HashMap<String,String> getPatientAndFillterDataToSendDoctor(int cmid);

    ArrayList<String> iFIsADoctorBuildMail(int cmid, String code,HashMap<String,String> data);
    boolean ifTypeISPatientOrGuardian(String code);

    /***********for func resendMail********************/

    HashMap<String, String> getUserByCmid(int cmid);
    ArrayList<String> generateMailForVerificationEmail(HashMap<String, String> details);
    public HashMap<Integer,HashMap<String,String>> BuildResponeWithOnlyRequestID(
            HashMap<String, String> details,
            String code);

    /***********for func responeDoctor********************/
    public HashMap<Integer,HashMap<String,String>> buildRejectMessage(int cmid, String Reason);
    public HashMap<Integer,HashMap<String,String>> proccesOfOkMember(int cmid);

    /***********for func SignIn********************/
    HashMap<Integer,HashMap<String,String>>verifySignIn(HashMap<String,String> details);


}