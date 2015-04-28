package registrationModule.src.api;

import java.util.HashMap;

/**
 * Created by NAOR on 06/04/2015.
 */
public interface IRegController extends IRegRequest,IRegVerify {

    Object handleReg(HashMap<String, String> filledForm);

}
