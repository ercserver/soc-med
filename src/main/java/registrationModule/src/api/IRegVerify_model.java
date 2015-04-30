package registrationModule.src.api;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify_model {

    Object VerifyDetail(int cmid);

    Object resendMail(int cmid);

    // if doctor reject we send reason in string reason
    //else we send null
    Object responeDoctor(int cmid,String reason);

    ArrayList<String> generateMailForVerification(HashMap<String, String> details);
}
