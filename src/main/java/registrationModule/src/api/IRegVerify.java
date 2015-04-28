package registrationModule.src.api;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify {

    //boolean verifyEmailWithCmid(String mail,int cmid);
    boolean VerifyDetail(int cmid);
    //boolean verifyDetailsDueToType(int userType);

    void resendMail(int cmid);

    // if doctor reject we send reason in string reason
    //else we send null
    void responeDoctor(int cmid,String reason);
}
