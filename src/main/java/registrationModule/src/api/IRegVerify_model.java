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
    HashMap<String, String> getUserByMail(String mail);


    boolean checkCondForResendMail(HashMap<String, String> details, String email, int cmid);

    /***********for func responeDoctor********************/
    HashMap<Integer,HashMap<String,String>> proccesOfOkMember(int cmid);

    /***********for func SignIn********************/
    HashMap<Integer,HashMap<String,String>>verifySignIn(HashMap<String,String> details);

    /***********for func responeToDoctorIfHeAccept********************/
    int checkIfDoctorIsaccept(String email);



    ArrayList<String> verifyFilledForm(HashMap<String, String> filledForm);

    HashMap<String,String> generateDataForAuth(HashMap<String, String> filledForm, int authMethod);//*
    HashMap<String, String> generateDataForAuthD(String access, String message, String subject, int authMethod);

}
