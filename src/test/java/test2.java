import registrationModule.src.api.IRegVerify_model;
import registrationModule.src.model.RegVerify_V1;

/**
 * Created by User on 16/04/2015.
 */
public class test2 {
    public static void main(String[] args) {
        IRegVerify_model v = new RegVerify_V1();
        //maccbi
        v.VerifyDetail(1002);

    }
}
