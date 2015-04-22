package registrationModule.src.api;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify {

    //boolean verifyEmailWithCmid(String mail,int cmid);
    public boolean VerifyDetail(int cmid);
    //boolean verifyDetailsDueToType(int userType);

    public void resendMail(int cmid);

    public void proccesOfOkMember(int cmid);
}
