package registrationModule.src.api;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegVerify_model {

    public boolean VerifyDetail(int cmid);
    //boolean verifyDetailsDueToType(int userType);

    public String resendMail(String mail,int cmid);

    public void proccesOfOkMember(int cmid);
}
