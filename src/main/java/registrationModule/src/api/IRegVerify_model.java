package registrationModule.src.api;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify_model {

    public boolean VerifyDetail(int cmid);
    //boolean verifyDetailsDueToType(int userType);

    public void resendMail(int cmid);

    // if doctor reject we send reason in string reason
    //else we send null
    public void responeDoctor(int cmid,String reason);
}
