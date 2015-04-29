package registrationModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify {

    //boolean verifyEmailWithCmid(String mail,int cmid);
    Object verifyDetail(HashMap<String, String> data);
    //boolean verifyDetailsDueToType(int userType);

    Object resendMail(HashMap<String, String> data);

    // if doctor reject we send reason in string reason
    //else we send null
    Object responeDoctor(HashMap<String, String> data);
}
