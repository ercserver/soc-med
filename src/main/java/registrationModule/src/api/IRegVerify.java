package registrationModule.src.api;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify {

    //boolean verifyEmailWithCmid(String mail,int cmid);
    Object VerifyDetail(int cmid);
    //boolean verifyDetailsDueToType(int userType);

    Object resendMail(int cmid);

    // if doctor reject we send reason in string reason
    //else we send null
    Object responeDoctor(int cmid,String reason);


}
