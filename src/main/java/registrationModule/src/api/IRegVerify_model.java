package registrationModule.src.api;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify_model {

    Object VerifyDetail(int cmid);

    Object resendAuth(int cmid);//TODO////need to get mail also!!

    // if doctor reject we send reason in string reason
    //else we send null
    Object responeDoctor(int cmid,String reason);


    ArrayList<String> verifyFilledForm(HashMap<String, String> filledForm);

    HashMap<String,String> generateDataForAuth(HashMap<String, String> filledForm, int authMethod);//*

    HashMap<Integer,HashMap<String,String>> verifySignIn(HashMap<String,String> details);
}
